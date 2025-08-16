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
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedQuantityFromStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;
    private final InventoryReserveProductCommandHandler inventoryReserveProductCommandHandler;
    private final InventoryReturnToStockReservedProductCommandHandler inventoryReturnToStockReservedProductCommandHandler;
    private final InventoryConfirmFromReserveCommandHandler inventoryConfirmFromReserveCommandHandler;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @KafkaListener(topics = {"product-topic", "order-topic"}, groupId = "inventory-group")
    public void listenPayments(DomainEvent event) {
        switch (event) {
            case ProductCreatedEvent productCreatedEvent -> inventoryRepository.save(Inventory.builder()
                    .availableQuantity(productCreatedEvent.getQuantity())
                    .productId(productCreatedEvent.getProductId())
                    .build());

            case ProductDeletedEvent productDeletedEvent ->
                    inventoryRepository.deleteByProductId(productDeletedEvent.getProductId());

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }

    @KafkaListener(topics = "saga-topic")
    public void listenSaga(DomainEvent event) {
        switch (event) {
            case StockCreatedEvent stockCreatedEvent -> {
                List<OrderedProductDTO> orderedProducts = stockCreatedEvent.getListOfProducts();
                try {
                    for (OrderedProductDTO product : orderedProducts) {
                        inventoryReserveProductCommandHandler
                                .handle(new InventoryReserveProductCommand(
                                        product.productId(), product.quantity(), stockCreatedEvent.getOrderId()));
                    }
                    kafkaTemplate.send("saga-topic", stockCreatedEvent.getOrderId().toString(),
                            StockReservedEvent.builder()
                                    .orderId(stockCreatedEvent.getOrderId())
                                    .userId(stockCreatedEvent.getCreatedBy())
                                    .orderPrice(stockCreatedEvent.getPrice())
                                    .userEmail(stockCreatedEvent.getUserEmail())
                                    .products(orderedProducts.stream()
                                            .map(p -> new ReservedProductDTO(p.productId(), p.quantity()))
                                            .toList())
                                    .build());
                } catch (OutOfStockException e) {
                    kafkaTemplate.send("saga-topic", stockCreatedEvent.getOrderId().toString(),
                            StockReservationFailedEvent.builder()
                                    .userEmail(stockCreatedEvent.getUserEmail())
                                    .orderId(stockCreatedEvent.getOrderId())
                                    .build());
                }
            }

            case FinalizeOrderEvent finalizeOrderEvent -> inventoryConfirmFromReserveCommandHandler
                    .handle(new InventoryConfirmFromReserveCommand(finalizeOrderEvent.getOrderId()));

            case OrderCanceledEvent orderCanceledEvent -> inventoryReturnToStockReservedProductCommandHandler.handle(
                    new InventoryReturnToStockReservedProductCommand(orderCanceledEvent.getOrderId()));

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}