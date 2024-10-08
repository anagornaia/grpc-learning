package org.example.grpc.client.order;

import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.grpc.contract.CombinedShipment;
import org.example.grpc.contract.Order;
import org.example.grpc.contract.OrderManagementGrpc;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class OrderManagementClient {

    private static final Logger logger = Logger.getLogger(OrderManagementClient.class.getName());

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(
                "localhost", 50051).usePlaintext().build();
        OrderManagementGrpc.OrderManagementBlockingStub stub = OrderManagementGrpc.newBlockingStub(channel);
        OrderManagementGrpc.OrderManagementStub asyncStub = OrderManagementGrpc.newStub(channel);

        Order order = Order
                .newBuilder()
                .setId("101")
                .addItems("iPhone XS").addItems("Mac Book Pro")
                .setDestination("San Jose, CA")
                .setPrice(2300f)
                .build();

        // Add Order
        StringValue result = stub.addOrder(order);
        logger.info("AddOrder Response -> : [%s]".formatted(result.getValue()));

        // Get Order
        StringValue id = StringValue.newBuilder().setValue("101").build();
        Order orderResponse = stub.getOrder(id);
        logger.info("GetOrder Response -> : [%s]".formatted(orderResponse.toString()));


        // Search Orders
        StringValue searchStr = StringValue.newBuilder().setValue("Google").build();
        Iterator<Order> matchingOrdersItr;
        matchingOrdersItr = stub.searchOrders(searchStr);
        while (matchingOrdersItr.hasNext()) {
            Order matchingOrder = matchingOrdersItr.next();
            logger.info("Search Order Response -> Matching Order - [%s]".formatted(matchingOrder.getId()));
            logger.info("Order : [%s]\n [%s]".formatted(order.getId(), matchingOrder));
        }


        // Update Orders
        invokeOrderUpdate(asyncStub);

        // Process Order
        invokeOrderProcess(asyncStub);


        // Search Orders
        StringValue searchStr2 = StringValue.newBuilder().setValue("Google").build();
        Iterator<Order> matchingOrdersItr2;
        matchingOrdersItr2 = stub.searchOrders(searchStr2);
        while (matchingOrdersItr2.hasNext()) {
            Order matchingOrder = matchingOrdersItr2.next();
            logger.info("Search Order Response -> Matching Order - [%s]".formatted(matchingOrder.getId()));
            logger.info("Order : [%s]\n [%s]".formatted(order.getId(), matchingOrder));
        }
    }


    private static void invokeOrderUpdate(OrderManagementGrpc.OrderManagementStub asyncStub) {

        Order updOrder1 = Order.newBuilder()
                .setId("102")
                .addItems("Google Pixel 3A").addItems("Google Pixel Book")
                .setDestination("Mountain View, CA")
                .setPrice(1100)
                .build();
        Order updOrder2 = Order.newBuilder()
                .setId("103")
                .addItems("Apple Watch S4").addItems("Mac Book Pro").addItems("iPad Pro")
                .setDestination("San Jose, CA")
                .setPrice(2800)
                .build();
        Order updOrder3 = Order.newBuilder()
                .setId("104")
                .addItems("Google Home Mini").addItems("Google Nest Hub").addItems("iPad Mini")
                .setDestination("Mountain View, CA")
                .setPrice(2200)
                .build();

        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<StringValue> updateOrderResponseObserver = new StreamObserver<StringValue>() {
            @Override
            public void onNext(StringValue value) {
                logger.info("Update Orders Res : " + value.getValue());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                logger.info("Update orders response completed!");
                finishLatch.countDown();
            }
        };

        StreamObserver<Order> updateOrderRequestObserver = asyncStub.updateOrders(updateOrderResponseObserver);
        updateOrderRequestObserver.onNext(updOrder1);
        updateOrderRequestObserver.onNext(updOrder2);
        updateOrderRequestObserver.onNext(updOrder3);
        updateOrderRequestObserver.onNext(updOrder3);


        if (finishLatch.getCount() == 0) {
            logger.warning("RPC completed or errored before we finished sending.");
            return;
        }
        updateOrderRequestObserver.onCompleted();

        // Receiving happens asynchronously

        try {
            if (!finishLatch.await(10, TimeUnit.SECONDS)) {
                logger.warning("FAILED : Process orders cannot finish within 10 seconds");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void invokeOrderProcess(OrderManagementGrpc.OrderManagementStub asyncStub) {

        final CountDownLatch finishLatch = new CountDownLatch(1);


        StreamObserver<CombinedShipment> orderProcessResponseObserver = new StreamObserver<CombinedShipment>() {
            @Override
            public void onNext(CombinedShipment value) {
                logger.info("Combined Shipment : " + value.getId() + " : " + value.getOrdersListList());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                logger.info("Order Processing completed!");
                finishLatch.countDown();
            }
        };

        StreamObserver<StringValue> orderProcessRequestObserver = asyncStub.processOrders(orderProcessResponseObserver);

        orderProcessRequestObserver.onNext(StringValue.newBuilder().setValue("102").build());
        orderProcessRequestObserver.onNext(StringValue.newBuilder().setValue("103").build());
        orderProcessRequestObserver.onNext(StringValue.newBuilder().setValue("104").build());
        orderProcessRequestObserver.onNext(StringValue.newBuilder().setValue("101").build());

        if (finishLatch.getCount() == 0) {
            logger.warning("RPC completed or errored before we finished sending.");
            return;
        }
        orderProcessRequestObserver.onCompleted();


        try {
            if (!finishLatch.await(120, TimeUnit.SECONDS)) {
                logger.warning("FAILED : Process orders cannot finish within 60 seconds");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
