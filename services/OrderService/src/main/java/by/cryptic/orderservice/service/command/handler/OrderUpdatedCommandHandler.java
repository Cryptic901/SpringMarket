package by.cryptic.orderservice.service.command.handler;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.orderservice.dto.ShortOrderDTO;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderUpdatedCommandHandler implements CommandHandler<OrderUpdateCommand> {

    private final CustomerOrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Retry(name = "orderRetry", fallbackMethod = "orderUpdateRetryFallback")
    @CachePut(cacheNames = "orders", key = "'order:' + #command.orderId()")
    public void handle(OrderUpdateCommand command) {
        CustomerOrder order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Order not found with id : %s".formatted(command.orderId())));
        if (order.getUserId().equals(command.userId()) && order.getOrderStatus().equals(OrderStatus.IN_PROGRESS)) {
            OrderMapper.updateEntity(order, new ShortOrderDTO(command.location()));
        } else {
            throw new IllegalArgumentException("Order should be in progress to update");
        }
        eventPublisher.publishEvent(OrderUpdatedEvent.builder()
                .orderId(order.getId())
                .userEmail(command.email())
                .status(OrderStatus.IN_PROGRESS)
                .build());
    }

    public void orderUpdateRetryFallback(OrderUpdateCommand orderUpdateCommand, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", orderUpdateCommand.orderId(), t.getMessage(), t);
        throw new UpdatingException("Failed to update order:" + orderUpdateCommand.orderId(), t);
    }
}
