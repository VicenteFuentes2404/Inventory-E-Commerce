package com.fullstack.inventory.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LowStockEvent {

    private UUID productId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private Integer minimumStock;
}
