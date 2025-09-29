package by.cryptic.inventoryservice.listener;

import by.cryptic.exceptions.OutOfStockException;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.repository.InventoryRepository;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;
    private final InventoryReserveProductCommandHandler inventoryReserveProductCommandHandler;
    private final InventoryReturnToStockReservedProductCommandHandler inventoryReturnToStockReservedProductCommandHandler;
    private final InventoryConfirmFromReserveCommandHandler inventoryConfirmFromReserveCommandHandler;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @KafkaListener(topics = {"product-topic", "order-topic"})
    public void listenProductsAndOrders(DomainEvent event) {
        switch (event) {
            case ProductCreatedEvent productCreatedEvent -> inventoryRepository.save(Inventory.builder()
                    .availableQuantity(productCreatedEvent.getQuantity())
                    .productId(productCreatedEvent.getProductId())
                    .build());

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
        log.info("-------------------------------------------");
        log.info("SAGA LISTENING IN INVENTORY EVENT LISTENER");
        log.info("!!!!!Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case StockCreatedEvent stockCreatedEvent -> {
                List<OrderedProductDTO> orderedProducts = stockCreatedEvent.getListOfProducts();
                log.info("List of ordered products: {}", orderedProducts.toString());
                try {
                    for (OrderedProductDTO product : orderedProducts) {
                        log.info("SAGA ORDERED PRODUCT: {}", product);
                        inventoryReserveProductCommandHandler
                                .handle(new InventoryReserveProductCommand(
                                        product.productId(), product.quantity(), stockCreatedEvent.getOrderId()));
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
                    log.info("SAGA StockReservedEvent {}", stockReservedEvent.getClass().getSimpleName());
                    kafkaTemplate.send("inventory-topic", stockCreatedEvent.getOrderId().toString(), stockReservedEvent);
                } catch (OutOfStockException e) {
                    log.warn("SAGA OUT OF STOCK EXCEPTION! {}, from class: by.cryptic.inventoryservice.listener", e.getMessage());
                    kafkaTemplate.send("inventory-topic", stockCreatedEvent.getOrderId().toString(),
                            StockReservationFailedEvent.builder()
                                    .userEmail(stockCreatedEvent.getUserEmail())
                                    .orderId(stockCreatedEvent.getOrderId())
                                    .build());
                } catch (Exception e) {
                    log.warn("SAGA EXCEPTION! {}, from class: by.cryptic.inventoryservice.listener", e.getMessage());
                    kafkaTemplate.send("inventory-topic", stockCreatedEvent.getOrderId().toString(),
                            StockReservationFailedEvent.builder()
                                    .userEmail(stockCreatedEvent.getUserEmail())
                                    .orderId(stockCreatedEvent.getOrderId())
                                    .build());
                }
            }

            case FinalizeOrderEvent finalizeOrderEvent -> {
                log.info("SAGA FINALIZE ORDER EVENT IN INVENTORY: {}", finalizeOrderEvent.getClass().getSimpleName());
                inventoryConfirmFromReserveCommandHandler
                        .handle(new InventoryConfirmFromReserveCommand(finalizeOrderEvent.getOrderId()));
            }

            case OrderFailedEvent orderFailedEvent -> {
                log.info("SAGA ORDER FAILED EVENT TO RETURN STOCK: {}", orderFailedEvent.getClass().getSimpleName());
                inventoryReturnToStockReservedProductCommandHandler.handle(
                        new InventoryReturnToStockReservedProductCommand(orderFailedEvent.getOrderId()));
            }

            default -> log.warn("Ignoring event {}", event);
        }
    }
}