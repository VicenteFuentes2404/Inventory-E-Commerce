package com.fullstack.inventory.controller;

import com.fullstack.inventory.dto.ProductRequestDTO;
import com.fullstack.inventory.dto.ProductResponseDTO;
import com.fullstack.inventory.dto.StockCheckResponseDTO;
import com.fullstack.inventory.model.Product;
import com.fullstack.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request
    ) {
        Product product = inventoryService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDTO.fromEntity(product));
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(
                inventoryService.getAllProducts()
                        .stream()
                        .map(ProductResponseDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable UUID productId
    ) {
        Product product = inventoryService.getProductById(productId);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(product));
    }

    @GetMapping("/{productId}/check-stock")
    public ResponseEntity<StockCheckResponseDTO> checkStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(inventoryService.checkStock(productId, quantity));
    }

    @PatchMapping("/{productId}/discount")
    public ResponseEntity<ProductResponseDTO> discountStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        Product product = inventoryService.discountStock(productId, quantity);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(product));
    }

    @PatchMapping("/{productId}/add")
    public ResponseEntity<ProductResponseDTO> addStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        Product product = inventoryService.addStock(productId, quantity);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(product));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts() {
        return ResponseEntity.ok(
                inventoryService.getLowStockProducts()
                        .stream()
                        .map(ProductResponseDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}