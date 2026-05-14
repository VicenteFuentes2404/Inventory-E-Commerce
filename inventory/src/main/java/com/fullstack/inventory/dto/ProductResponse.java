package com.fullstack.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fullstack.inventory.model.Product;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private UUID id;
    private String name;
    private String sku;
    private Integer stock;
    private Integer minimumStock;
    private BigDecimal price;
    private Boolean active;
    private LocalDateTime createdAt;
    private Boolean lowStock;

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .stock(product.getStock())
                .minimumStock(product.getMinimumStock())
                .price(product.getPrice())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .lowStock(product.getStock() <= product.getMinimumStock())
                .build();
    }
}