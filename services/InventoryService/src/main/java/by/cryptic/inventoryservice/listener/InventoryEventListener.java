package by.cryptic.inventoryservice.listener;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.command.InventoryConfirmFromReserveCommand;
import by.cryptic.inventoryservice.service.command.InventoryReserveProductCommand;
import by.cryptic.inventoryservice.service.command.InventoryReturnToStockReservedProductCommand;
import by.cryptic.inventoryservice.service.command.handler.InventoryConfirmFromReserveCommandHandler;
import by.cryptic.inventoryservice.service.command.handler.InventoryReserveProductCommandHandler;
import by.cryptic.inventoryservice.service.command.handler.InventoryReturnToStockReservedProductCommandHandler;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.DTO.ReservedProductDTO;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.inventory.StockCreatedEvent;
import by.cryptic.utils.event.inventory.StockReservationFailedEvent;
import by.cryptic.utils.event.inventory.StockReservedEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;
    private final InventoryReserveProductCommandHandler inventoryReserveProductCommandHandler;
    private final InventoryReturnToStockReservedProductCommandHandler inventoryReturnToStockReservedProductCommandHandler;
    private final InventoryConfirmFromReserveCommandHandler inventoryConfirmFromReserveCommandHandler;
    private final WarehouseRepository warehouseRepository;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @KafkaListener(topics = {"product-topic", "order-topic"})
    @Transactional
    public void listenProductsAndOrders(DomainEvent event) {
        log.debug("Received event: {}", event.getClass().getSimpleName());
        switch (event) {
            case ProductCreatedEvent productCreatedEvent -> {
                try {
                    Warehouse warehouse = findClosestWarehouseAndRebalanceProducts(productCreatedEvent);
                    inventoryRepository.save(Inventory.builder()
                            .availableQuantity(productCreatedEvent.getQuantity())
                            .warehouse(warehouse)
                            .productId(productCreatedEvent.getProductId())
                            .build());
                } catch (Exception e) {
                    log.error("Exception while creating product {}", e.getMessage());
                    throw new CreatingException("Exception while creating product " + e.getMessage(), e);
                }
            }

            case ProductUpdatedEvent productUpdatedEvent -> {
                Inventory inventory = inventoryRepository.findByProductId(productUpdatedEvent
                        .getProductId()).orElseThrow(() -> new EntityNotFoundException(
                        "Inventory not found with productId: " + productUpdatedEvent.getProductId()));
                inventory.setAvailableQuantity(productUpdatedEvent.getQuantity());
                inventoryRepository.save(inventory);
            }

            case ProductDeletedEvent productDeletedEvent ->
                    inventoryRepository.deleteByProductId(productDeletedEvent.getProductId());

            default -> log.warn("Unexpected event type: {}", event);
        }
    }

    @KafkaListener(topics = "saga-topic")
    public void listenSaga(DomainEvent event) {
        log.info("Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case StockCreatedEvent stockCreatedEvent -> {
                List<OrderedProductDTO> orderedProducts = stockCreatedEvent.getListOfProducts();
                try {
                    for (OrderedProductDTO product : orderedProducts) {
                        inventoryReserveProductCommandHandler
                                .handle(new InventoryReserveProductCommand(
                                        product.productId(), product.quantity(),
                                        stockCreatedEvent.getOrderId(),
                                        stockCreatedEvent.getLon(),
                                        stockCreatedEvent.getLat()));
                    }
                    StockReservedEvent stockReservedEvent = StockReservedEvent.builder()
                            .orderId(stockCreatedEvent.getOrderId())
                            .userId(stockCreatedEvent.getCreatedBy())
                            .orderPrice(stockCreatedEvent.getPrice())
                            .paymentMethod(stockCreatedEvent.getPaymentMethod())
                            .userEmail(stockCreatedEvent.getUserEmail())
                            .products(orderedProducts.stream()
                                    .map(p -> new ReservedProductDTO(p.productId(), p.quantity()))
                                    .toList())
                            .build();
                    kafkaTemplate.send("inventory-topic", stockCreatedEvent.getOrderId().toString(), stockReservedEvent);
                } catch (Exception e) {
                    kafkaTemplate.send("inventory-topic", stockCreatedEvent.getOrderId().toString(),
                            StockReservationFailedEvent.builder()
                                    .userEmail(stockCreatedEvent.getUserEmail())
                                    .orderId(stockCreatedEvent.getOrderId())
                                    .build());
                }
            }

            case FinalizeOrderEvent finalizeOrderEvent -> inventoryConfirmFromReserveCommandHandler
                    .handle(new InventoryConfirmFromReserveCommand(finalizeOrderEvent.getOrderId()));

            case OrderFailedEvent orderFailedEvent -> inventoryReturnToStockReservedProductCommandHandler.handle(
                    new InventoryReturnToStockReservedProductCommand(orderFailedEvent.getOrderId()));

            default -> log.warn("Ignoring event {}", event);
        }
    }

    private Warehouse findClosestWarehouseAndRebalanceProducts(ProductCreatedEvent productCreatedEvent) {
                        /*
               //TODO GET LATITUDE AND LONGITUDE FROM FRONTEND
                    this variant generates random numbers of lat and lon in range of Belarus
                 */
        double latitude = ThreadLocalRandom.current().nextDouble(51.2, 56.2);
        double longitude = ThreadLocalRandom.current().nextDouble(23.2, 32.7);

        Warehouse warehouse = warehouseRepository.findClosestWarehousesWithEnoughSpace(latitude,
                longitude, productCreatedEvent.getQuantity());
        if (warehouse == null) {
            throw new IllegalStateException("There are no warehouses with enough space for products");
        }
        warehouse.setCurrentLoad(warehouse.getCurrentLoad() + productCreatedEvent.getQuantity());
        warehouse.setCapacity(warehouse.getCapacity() - productCreatedEvent.getQuantity());
        return warehouseRepository.save(warehouse);
    }
}