package org.example.grpc.server.product;

import org.example.grpc.contract.Product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductService {

    private final Map<String, Product> productMap;

    public ProductService() {
        productMap = new HashMap<>();
    }

    public String addProduct(Product request) {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        request = request.toBuilder().setId(randomUUIDString).build();
        productMap.put(randomUUIDString, request);
        return randomUUIDString;
    }

    public Product getByUuid(String id) {
        if (!productMap.containsKey(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        return productMap.get(id);
    }
}
