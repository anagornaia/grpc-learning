package org.example.grpc.server.product;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import org.example.grpc.contract.*;
import org.example.grpc.contract.utils.ProtoUtils;

import java.util.logging.Logger;

public class ProductInfoImpl extends ProductInfoGrpc.ProductInfoImplBase {

    private static final Logger logger = Logger.getLogger(ProductInfoImpl.class.getName());

    private static final ProductService productService = new ProductService();

    @Override
    public void addProduct(Product request, io.grpc.stub.StreamObserver<ProductId> responseObserver) {

        logger.info("Received request: [%s]".formatted(request));

        String randomUUIDString = productService.addProduct(request);

        logger.info("Saved product with uuid: [%s]".formatted(randomUUIDString));

        ProductId id = ProductId.newBuilder().setId(ProtoUtils.convertToStringValue(randomUUIDString)).build();
        responseObserver.onNext(id);
        responseObserver.onCompleted();
    }

    @Override
    public void getProduct(ProductId request, io.grpc.stub.StreamObserver<Product> responseObserver) {
        logger.info("Received request: [%s]".formatted(request));

        String id = request.getId().getValue();

        try {
            Product product = productService.getByUuid(id);
            logger.info("Found product");

            responseObserver.onNext(product);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            logger.info("Product not found");

            responseObserver.onError(new StatusException(Status.NOT_FOUND));
        }

    }

}