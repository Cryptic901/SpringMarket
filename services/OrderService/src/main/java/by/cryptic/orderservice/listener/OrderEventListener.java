package by.cryptic.orderservice.listener;


import by.cryptic.utils.OrderStatus;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.utils.event.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderViewRepository orderViewRepository;
    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void listenOrders(String rawEvent) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        switch (EventType.valueOf(type)) {
            case OrderCreatedEvent -> {
                OrderCreatedEvent event = objectMapper.treeToValue(node, OrderCreatedEvent.class);
                orderViewRepository.save(CustomerOrderView.builder()
                        .orderStatus(OrderStatus.IN_PROGRESS)
                        .orderId(event.getOrderId())
                        .createdBy(event.getCreatedBy())
                        .location(event.getLocation())
                        .createdAt(event.getCreatedTimestamp())
                        .price(event.getPrice())
                        .build());
            }
            case OrderUpdatedEvent -> {
                OrderUpdatedEvent event = objectMapper.treeToValue(node, OrderUpdatedEvent.class);
                orderViewRepository.findById(event.getOrderId()).ifPresent(orderView -> {
                    orderMapper.updateView(orderView, event);
                    orderViewRepository.save(orderView);
                });
            }
            case OrderCanceledEvent -> {
                OrderCanceledEvent event = objectMapper.treeToValue(node, OrderCanceledEvent.class);
                orderViewRepository.findById(event.getOrderId()).ifPresent(_ ->
                        orderViewRepository.deleteById(event.getOrderId()));
            }
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}
