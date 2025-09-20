package by.cryptic.orderservice.listener;


import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.publisher.OrderEventPublisher;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderViewRepository orderViewRepository;
    private final CustomerOrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @KafkaListener(topics = "order-topic")
    @Transactional
    public void listenOrders(DomainEvent event) {
        switch (event) {
            case OrderSuccessEvent orderSuccessEvent -> {
                log.info("SAGA ORDER SUCCESS! {}, from class: by.cryptic.orderservice.listener", orderSuccessEvent.getClass().getSimpleName());
                updateOrderStatus(orderSuccessEvent.getOrderStatus(),
                        orderSuccessEvent.getOrderId());
            }
            case OrderFailedEvent orderFailedEvent -> {
                log.info("SAGA ORDER FAILED! {}, from class: by.cryptic.orderservice.listener", orderFailedEvent.getClass().getSimpleName());
                updateOrderStatus(orderFailedEvent.getOrderStatus(),
                        orderFailedEvent.getOrderId());
            }

            case OrderCanceledEvent orderCanceledEvent -> {
                log.info("SAGA ORDER CANCELLED! {}, from class: by.cryptic.orderservice.listener", orderCanceledEvent.getClass().getSimpleName());
                updateOrderStatus(orderCanceledEvent.getOrderStatus(),
                        orderCanceledEvent.getOrderId());
            }
            default -> log.warn("Unexcepted event type: {}", event);
        }
    }

    public void updateOrderStatus(OrderStatus orderStatus, UUID orderId) {
        CustomerOrderView customerOrderView = orderViewRepository
                .findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id "
                        + orderId));
        CustomerOrder customerOrder = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id "
                        + orderId));
        customerOrderView.setOrderStatus(orderStatus);
        customerOrder.setOrderStatus(orderStatus);
        orderViewRepository.save(customerOrderView);
        orderRepository.save(customerOrder);
    }

    @KafkaListener(topics = "saga-topic")
    @Transactional
    public void listenSaga(DomainEvent event) {
        log.info("-------------------------------------------");
        log.info("SAGA LISTENING IN ORDER EVENT LISTENER");
        log.info("!!!!!Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case OrderFailedEvent orderFailedEvent -> {
                log.info("SAGA ORDER FAILED! {}. REASON: {}," +
                                " from class: by.cryptic.orderservice.listener",
                        orderFailedEvent.getClass().getSimpleName(), orderFailedEvent.getFailureReason());
                CustomerOrder order = orderRepository.findById(orderFailedEvent.getOrderId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                ("Order with id %s not found"
                                        .formatted(orderFailedEvent.getOrderId()))));
                order.setPaymentId(null);
                order.setOrderStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                log.info("Order after saving: {}", order);

                CustomerOrderView orderView = CustomerOrderView.builder()
                        .orderId(orderFailedEvent.getOrderId())
                        .paymentId(order.getPaymentId())
                        .orderStatus(orderFailedEvent.getOrderStatus())
                        .createdBy(order.getCreatedBy())
                        .createdAt(order.getCreatedAt())
                        .updatedBy(order.getUpdatedBy())
                        .updatedAt(order.getUpdatedAt())
                        .location(order.getLocation())
                        .price(order.getPrice())
                        .build();
                orderViewRepository.save(orderView);
                log.info("OrderView after saving: {}", orderView);
                log.info("ORDER FAILED EVENT WAS SAVED: {}", order);
            }

            case FinalizeOrderEvent finalizeOrderEvent -> {
                log.info("SAGA COMPLETED SUCCESSFULLY! {}, from class: by.cryptic.orderservice.listener", finalizeOrderEvent.getClass().getSimpleName());
                CustomerOrder order = orderRepository.findById(finalizeOrderEvent.getOrderId())
                        .orElseThrow(() -> new EntityNotFoundException(("Order with id %s not found"
                                .formatted(finalizeOrderEvent.getOrderId()))));
                order.setPaymentId(finalizeOrderEvent.getPaymentId());
                order.setOrderStatus(finalizeOrderEvent.getOrderStatus());
                orderRepository.save(order);

                CustomerOrderView orderView = CustomerOrderView.builder()
                        .orderId(finalizeOrderEvent.getOrderId())
                        .paymentId(finalizeOrderEvent.getPaymentId())
                        .orderStatus(finalizeOrderEvent.getOrderStatus())
                        .createdBy(order.getCreatedBy())
                        .createdAt(order.getCreatedAt())
                        .updatedBy(order.getUpdatedBy())
                        .updatedAt(order.getUpdatedAt())
                        .location(order.getLocation())
                        .price(order.getPrice())
                        .build();
                orderViewRepository.save(orderView);

                orderEventPublisher.sentOrderSuccessEvent(order);
            }
            default -> log.warn("Ignoring event {}", event.getClass().getSimpleName());
        }
    }
}