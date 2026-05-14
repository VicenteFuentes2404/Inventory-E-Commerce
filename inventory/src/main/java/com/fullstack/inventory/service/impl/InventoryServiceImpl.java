package com.fullstack.inventory.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fullstack.inventory.dto.CreateProductRequest;
import com.fullstack.inventory.event.OrderCreatedEvent;
import com.fullstack.inventory.model.Product;
import com.fullstack.inventory.publisher.InventoryEventPublisher;
import com.fullstack.inventory.repository.InventoryService;
import com.fullstack.inventory.repository.ProductRepository;


@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryEventPublisher inventoryEventPublisher;

    public InventoryServiceImpl(
            ProductRepository productRepository,
            InventoryEventPublisher inventoryEventPublisher
    ) {
        this.productRepository = productRepository;
        this.inventoryEventPublisher = inventoryEventPublisher;
    }

    @Override
    public Product createProduct(CreateProductRequest request) {
        productRepository.findBySku(request.getSku()).ifPresent(product -> {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un producto con el SKU: " + request.getSku()
            );
        });

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .stock(request.getStock())
                .minimumStock(request.getMinimumStock())
                .price(request.getPrice())
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);

        if (savedProduct.getStock() <= savedProduct.getMinimumStock()) {
            inventoryEventPublisher.publishLowStock(savedProduct);
        }

        return savedProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado con ID: " + id
                ));
    }

    @Override
    public Product updateStock(UUID productId, Integer stock) {
        if (stock < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El stock no puede ser negativo"
            );
        }

        Product product = getProductById(productId);
        product.setStock(stock);

        Product updatedProduct = productRepository.save(product);

        if (updatedProduct.getStock() <= updatedProduct.getMinimumStock()) {
            inventoryEventPublisher.publishLowStock(updatedProduct);
        }

        return updatedProduct;
    }

    @Override
    public Product discountStockFromOrder(OrderCreatedEvent event) {
        Product product = getProductById(event.getProductId());

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El producto no está activo"
            );
        }

        if (product.getStock() < event.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente para el producto: " + product.getName()
            );
        }

        product.setStock(product.getStock() - event.getQuantity());

        Product updatedProduct = productRepository.save(product);

        System.out.println("INVENTORY: Stock descontado del producto " + updatedProduct.getName());
        System.out.println("INVENTORY: Stock actual: " + updatedProduct.getStock());

        if (updatedProduct.getStock() <= updatedProduct.getMinimumStock()) {
            inventoryEventPublisher.publishLowStock(updatedProduct);
        }

        return updatedProduct;
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getStock() <= product.getMinimumStock())
                .collect(Collectors.toList());
    }
}