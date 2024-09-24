package org.example.grpc.client.product;

import org.example.grpc.contract.Product;
import org.example.grpc.contract.utils.ProtoUtils;

public class ProductDataGeneratorService {

    public Product generateProduct() {
        return Product.newBuilder()
                .setName(ProtoUtils.convertToStringValue("Samsung S10"))
                .setDescription(ProtoUtils.convertToStringValue("Samsung S10 is a smart phone"))
                .setPrice(ProtoUtils.convertToStringValue(700.0f))
                .build();
    }
}
