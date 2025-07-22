package by.cryptic.orderservice.listener;


import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderViewRepository orderViewRepository;
    private final OrderMapper orderMapper;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void listenOrders(DomainEvent event) {
        switch (event) {
            case OrderSuccessEvent orderSuccessEvent -> orderViewRepository.save(CustomerOrderView.builder()
                    .orderStatus(OrderStatus.IN_PROGRESS)
                    .orderId(orderSuccessEvent.getOrderId())
                    .createdBy(orderSuccessEvent.getCreatedBy())
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

            case OrderUpdatedEvent orderUpdatedEvent ->
                    orderViewRepository.findById(orderUpdatedEvent.getOrderId()).ifPresent(orderView -> {
                        orderMapper.updateView(orderView, orderUpdatedEvent);
                        orderViewRepository.save(orderView);
                    });

            case OrderCanceledEvent orderCanceledEvent ->
                    orderViewRepository.findById(orderCanceledEvent.getOrderId()).ifPresent(_ ->
                            orderViewRepository.deleteById(orderCanceledEvent.getOrderId()));

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}