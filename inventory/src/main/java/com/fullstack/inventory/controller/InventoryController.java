package com.fullstack.inventory.controller;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.inventory.dto.CreateProductRequest;
import com.fullstack.inventory.dto.ProductResponse;
import com.fullstack.inventory.model.Product;
import com.fullstack.inventory.repository.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = inventoryService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromEntity(product));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(
                inventoryService.getAllProducts()
                        .stream()
                        .map(ProductResponse::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {
        Product product = inventoryService.getProductById(productId);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable UUID productId,
            @RequestParam Integer stock
    ) {
        Product product = inventoryService.updateStock(productId, stock);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts() {
        return ResponseEntity.ok(
                inventoryService.getLowStockProducts()
                        .stream()
                        .map(ProductResponse::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}