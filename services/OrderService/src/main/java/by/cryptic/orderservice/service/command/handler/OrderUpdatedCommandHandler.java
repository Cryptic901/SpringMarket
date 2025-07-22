package by.cryptic.orderservice.service.command.handler;

import by.cryptic.orderservice.dto.ShortOrderDTO;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderUpdatedCommandHandler implements CommandHandler<OrderUpdateCommand> {

    private final CustomerOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    @Retry(name = "orderRetry", fallbackMethod = "orderUpdatedFallback")
    @Bulkhead(name = "orderBulkhead", fallbackMethod = "orderUpdatedFallback")
    public void handle(OrderUpdateCommand command) {
        CustomerOrder order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Order not found with id : %s".formatted(command.orderId())));
        if (order.getUserId().equals(command.userId()) && order.getOrderStatus().equals(OrderStatus.IN_PROGRESS)) {
            orderMapper.updateEntity(order, new ShortOrderDTO(command.location()));
        } else {
            throw new IllegalArgumentException("Order should be in progress to update");
        }
        eventPublisher.publishEvent(OrderUpdatedEvent.builder()
                .orderId(order.getId())
                .userEmail(command.email())
                .status(OrderStatus.IN_PROGRESS)
                .build());
        Objects.requireNonNull(cacheManager.getCache("orders"))
                .put("order:" + command.location() + '-' + command.userId(), order);
    }

    public void orderUpdatedFallback(OrderUpdateCommand orderUpdateCommand, Throwable t) {
        log.error("Failed to cancel {}, {}", orderUpdateCommand.orderId(), t);
        throw new RuntimeException(t.getMessage());
    }
}
