package com.fullstack.inventory.observer;

import com.fullstack.inventory.event.LowStockEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LowStockObserver {

    @EventListener
    public void handleLowStock(LowStockEvent event) {
        System.out.println("INVENTORY OBSERVER: Stock bajo detectado");
        System.out.println("Producto: " + event.getProductName());
        System.out.println("SKU: " + event.getSku());
        System.out.println("Stock actual: " + event.getCurrentStock());
        System.out.println("Stock mínimo: " + event.getMinimumStock());
        System.out.println("SIMULACIÓN NOTIFICATIONS-MS: Enviar alerta al administrador");
    }
}
