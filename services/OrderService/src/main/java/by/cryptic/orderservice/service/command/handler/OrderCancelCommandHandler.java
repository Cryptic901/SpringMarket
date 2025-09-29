package by.cryptic.orderservice.service.command.handler;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.exceptions.UpdatingException;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.publisher.OrderEventPublisher;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.utils.handler.CommandHandler;
import by.cryptic.utils.enums.OrderStatus;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCancelCommandHandler implements CommandHandler<OrderCancelCommand> {

    private final CustomerOrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "orders", key = "'order:' + #command.orderId()")
    public void handle(OrderCancelCommand command) {
        log.info("Handling order cancel command {}", command);

        CustomerOrder order = validateAndGetOrder(command);

        saveOrder(order);

        orderEventPublisher.cancelOrderWithRetry(order, command);
    }

    private CustomerOrder validateAndGetOrder(OrderCancelCommand command) {
        CustomerOrder order = orderRepository.findById(command.orderId())
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
        return order;
    }

    @Retry(name = "orderRetry", fallbackMethod = "orderSaveCancelRetryFallback")
    public void saveOrder(CustomerOrder order) {
        orderRepository.save(order);
    }

    public void orderSaveCancelRetryFallback(CustomerOrder order, Throwable t) {
        log.error("Failed to save {} after all retry attempts. Cause: {}", order.getId(), t.getMessage(), t);
        throw new UpdatingException("Failed to save cancelling:" + order.getId(), t);
    }
}
