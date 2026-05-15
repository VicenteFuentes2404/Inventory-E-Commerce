package com.fullstack.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LowStockEvent {

    private UUID productId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private Integer minimumStock;
}