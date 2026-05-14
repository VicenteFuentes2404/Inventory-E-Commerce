package com.fullstack.inventory.observer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.inventory.event.OrderCreatedEvent;
import com.fullstack.inventory.repository.InventoryService;

@RestController
@RequestMapping("/api/inventory/observer")
@CrossOrigin(origins = "*")
public class OrderCreatedObserver {

    private final InventoryService inventoryService;

    public OrderCreatedObserver(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/order-created")
    public ResponseEntity<Void> handleOrderCreated(@RequestBody OrderCreatedEvent event) {
        System.out.println("INVENTORY OBSERVER: Evento OrderCreated recibido desde Orders");
        System.out.println("INVENTORY OBSERVER: Descontando stock del producto " + event.getProductId());

        inventoryService.discountStockFromOrder(event);

        System.out.println("INVENTORY OBSERVER: Stock actualizado correctamente");

        return ResponseEntity.ok().build();
    }
}