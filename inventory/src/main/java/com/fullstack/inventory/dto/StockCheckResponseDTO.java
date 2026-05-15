package com.fullstack.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockCheckResponseDTO {

    private boolean available;
    private Integer currentStock;
    private Integer requestedQuantity;
}
