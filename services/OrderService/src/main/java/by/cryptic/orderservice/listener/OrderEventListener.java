package by.cryptic.orderservice.listener;


import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderViewRepository orderViewRepository;
    private final CustomerOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void listenOrders(DomainEvent event) {
        switch (event) {
            case OrderSuccessEvent orderSuccessEvent -> orderViewRepository.save(CustomerOrderView.builder()
                    .orderStatus(orderSuccessEvent.getOrderStatus())
                    .orderId(orderSuccessEvent.getOrderId())
                    .createdBy(orderSuccessEvent.getCreatedBy())
                    .createdAt(orderSuccessEvent.getTimestamp())
                    .location(orderSuccessEvent.getLocation())
                    .price(orderSuccessEvent.getPrice())
                    .build());

            case OrderFailedEvent orderFailedEvent -> orderViewRepository.save(CustomerOrderView.builder()
                    .orderStatus(OrderStatus.FAILED)
                    .orderId(orderFailedEvent.getOrderId())
                    .createdBy(orderFailedEvent.getCreatedBy())
                    .location(orderFailedEvent.getLocation())
                    .price(orderFailedEvent.getPrice())
                    .build());

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }

    @KafkaListener(topics = "saga-topic")
    public void listenSaga(DomainEvent event) {
        switch (event) {
            case OrderCanceledEvent orderCanceledEvent -> {
                CustomerOrder order = orderRepository.findById(orderCanceledEvent.getOrderId())
                        .orElseThrow(() -> new EntityNotFoundException(("Order with id %s not found"
                                .formatted(orderCanceledEvent.getOrderId()))));
                order.setPaymentId(null);
                order.setOrderStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                eventPublisher.publishEvent(OrderFailedEvent.builder()
                        .orderStatus(order.getOrderStatus())
                        .orderId(order.getId())
                        .createdBy(order.getCreatedBy())
                        .listOfProducts(order.getProducts().stream().map(orderMapper::toOrderedDto).toList())
                        .location(order.getLocation())
                        .userEmail(order.getUserEmail())
                        .build());
            }
            case FinalizeOrderEvent finalizeOrderEvent -> {
                CustomerOrder order = orderRepository.findById(finalizeOrderEvent.getOrderId())
                        .orElseThrow(() -> new EntityNotFoundException(("Order with id %s not found"
                                .formatted(finalizeOrderEvent.getOrderId()))));
                order.setPaymentId(finalizeOrderEvent.getPaymentId());
                order.setOrderStatus(OrderStatus.IN_PROGRESS);
                orderRepository.save(order);
                eventPublisher.publishEvent(OrderSuccessEvent.builder()
                        .orderStatus(order.getOrderStatus())
                        .orderId(order.getId())
                        .createdBy(order.getCreatedBy())
                        .listOfProducts(order.getProducts().stream().map(orderMapper::toOrderedDto).toList())
                        .location(order.getLocation())
                        .userEmail(order.getUserEmail())
                        .build());
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }
}