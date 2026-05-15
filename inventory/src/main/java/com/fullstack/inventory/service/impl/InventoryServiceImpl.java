package com.fullstack.inventory.service.impl;

import com.fullstack.inventory.dto.ProductRequestDTO;
import com.fullstack.inventory.dto.StockCheckResponseDTO;
import com.fullstack.inventory.event.LowStockEvent;
import com.fullstack.inventory.model.Product;
import com.fullstack.inventory.model.StockMovement;
import com.fullstack.inventory.repository.ProductRepository;
import com.fullstack.inventory.repository.StockMovementRepository;
import com.fullstack.inventory.service.InventoryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryServiceImpl(
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Product createProduct(ProductRequestDTO request) {
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

        registerMovement(
                savedProduct.getId(),
                savedProduct.getStock(),
                "INITIAL",
                "Stock inicial del producto"
        );

        publishLowStockIfNeeded(savedProduct);

        return savedProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado con ID: " + productId
                ));
    }

    @Override
    public StockCheckResponseDTO checkStock(UUID productId, Integer quantity) {
        Product product = getProductById(productId);

        boolean available = Boolean.TRUE.equals(product.getActive())
                && product.getStock() >= quantity;

        return new StockCheckResponseDTO(
                available,
                product.getStock(),
                quantity
        );
    }

    @Override
    public Product discountStock(UUID productId, Integer quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a 0"
            );
        }

        Product product = getProductById(productId);

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El producto no está activo"
            );
        }

        if (product.getStock() < quantity) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente para el producto: " + product.getName()
            );
        }

        product.setStock(product.getStock() - quantity);

        Product updatedProduct = productRepository.save(product);

        registerMovement(
                updatedProduct.getId(),
                quantity,
                "OUT",
                "Descuento de stock por compra"
        );

        publishLowStockIfNeeded(updatedProduct);

        return updatedProduct;
    }

    @Override
    public Product addStock(UUID productId, Integer quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a 0"
            );
        }

        Product product = getProductById(productId);
        product.setStock(product.getStock() + quantity);

        Product updatedProduct = productRepository.save(product);

        registerMovement(
                updatedProduct.getId(),
                quantity,
                "IN",
                "Ingreso manual de stock"
        );

        return updatedProduct;
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getStock() <= product.getMinimumStock())
                .collect(Collectors.toList());
    }

    private void registerMovement(UUID productId, Integer quantity, String type, String description) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .quantity(quantity)
                .type(type)
                .description(description)
                .build();

        stockMovementRepository.save(movement);
    }

    private void publishLowStockIfNeeded(Product product) {
        if (product.getStock() <= product.getMinimumStock()) {
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
}