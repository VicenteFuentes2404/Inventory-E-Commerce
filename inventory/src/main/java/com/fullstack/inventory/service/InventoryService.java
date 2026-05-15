package com.fullstack.inventory.service;

import com.fullstack.inventory.dto.ProductRequestDTO;
import com.fullstack.inventory.dto.StockCheckResponseDTO;
import com.fullstack.inventory.model.Product;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    Product createProduct(ProductRequestDTO request);

    List<Product> getAllProducts();

    Product getProductById(UUID productId);

    StockCheckResponseDTO checkStock(UUID productId, Integer quantity);

    Product discountStock(UUID productId, Integer quantity);

    Product addStock(UUID productId, Integer quantity);

    List<Product> getLowStockProducts();
}