package org.example.grpc.client.product;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.contract.*;

import java.util.logging.Logger;

public class ProductInfoClient {

    private static final Logger logger = Logger.getLogger(ProductInfoClient.class.getName());
    private static final ProductDataGeneratorService productDataGeneratorService = new ProductDataGeneratorService();


    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ProductInfoGrpc.ProductInfoBlockingStub stub = ProductInfoGrpc.newBlockingStub(channel);

        ProductId productId1 = stub.addProduct(
                productDataGeneratorService.generateProduct()
        );
        logger.info("Product ID: [%s] added successfully.".formatted(productId1.getId()));

        Product product1 = stub.getProduct(productId1);
        logger.info("Product: [%s]".formatted(product1.toString()));
//-------------------------------------------------------------------------------------------------------
        ProductId productId2 = stub.addProduct(
                productDataGeneratorService.generateProduct()
        );
        logger.info("Product ID: [%s] added successfully.".formatted(productId2.getId()));

        Product product2 = stub.getProduct(productId2);
        logger.info("Product: [%s]".formatted(product2.toString()));

//        GetProductsByCriteriaResponse products = stub.getAllProducts(GetProductsByCriteriaRequest.newBuilder().build());
//
//        logger.info("Products: \n[%s]".formatted(products.getProductsList().toString()));

        channel.shutdown();
    }


}
