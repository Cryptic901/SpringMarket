package by.cryptic.inventoryservice.listener;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "product-topic", groupId = "inventory-group")
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
            case OrderCreatedEvent orderCreatedEvent -> {

            }

            case OrderCanceledEvent orderCanceledEvent -> {
            }

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}