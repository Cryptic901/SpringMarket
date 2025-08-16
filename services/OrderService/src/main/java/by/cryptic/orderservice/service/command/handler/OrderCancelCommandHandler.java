package by.cryptic.orderservice.service.command.handler;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCancelCommandHandler implements CommandHandler<OrderCancelCommand> {

    private final CustomerOrderRepository customerOrderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomerOrderRepository orderRepository;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "orders", key = "'order:' + #command.orderId()")
    @Retry(name = "orderRetry", fallbackMethod = "orderCancelRetryFallback")
    public void handle(OrderCancelCommand command) {
        log.info("Handling order cancel command {}", command);
        CustomerOrder order = customerOrderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Order not found with id : %s".formatted(command.orderId())));

        if (order.getUserId().equals(command.userId()) &&
                order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            log.info("Order cancelled successfully");
        } else {
            log.error("Order cannot be cancelled");
            throw new IllegalStateException("Order not completed");
        }
        orderRepository.save(order);
        eventPublisher.publishEvent(new OrderCanceledEvent(order.getId(), command.email()));
    }

    public void orderCancelRetryFallback(OrderCancelCommand orderCancelCommand, Throwable t) {
        log.error("Failed to cancel {} after all retry attempts. Cause: {}", orderCancelCommand.orderId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete order:" + orderCancelCommand.orderId(), t);
    }

}
