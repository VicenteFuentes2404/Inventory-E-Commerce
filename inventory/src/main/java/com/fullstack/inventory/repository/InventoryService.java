package com.fullstack.inventory.repository;

import java.util.List;
import java.util.UUID;

import com.fullstack.inventory.dto.CreateProductRequest;
import com.fullstack.inventory.event.OrderCreatedEvent;
import com.fullstack.inventory.model.Product;

public interface InventoryService {

    Product createProduct(CreateProductRequest request);

    List<Product> getAllProducts();

    Product getProductById(UUID id);

    Product updateStock(UUID productId, Integer stock);

    Product discountStockFromOrder(OrderCreatedEvent event);

    List<Product> getLowStockProducts();
}