package com.fullstack.inventory.dto;

import com.fullstack.inventory.model.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProductResponseDTO {

    private UUID id;
    private String name;
    private String sku;
    private Integer stock;
    private Integer minimumStock;
    private BigDecimal price;
    private Boolean active;
    private Boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponseDTO fromEntity(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .stock(product.getStock())
                .minimumStock(product.getMinimumStock())
                .price(product.getPrice())
                .active(product.getActive())
                .lowStock(product.getStock() <= product.getMinimumStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}