package com.fullstack.inventory.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fullstack.inventory.event.LowStockEvent;
import com.fullstack.inventory.model.Product;

@Component
public class InventoryEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public InventoryEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishLowStock(Product product) {
        LowStockEvent event = new LowStockEvent(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getStock(),
                product.getMinimumStock()
        );

        eventPublisher.publishEvent(event);
    }
}