package by.cryptic.springmarket.service.command.handler.order;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.event.order.OrderCanceledEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.CustomerOrder;
import by.cryptic.springmarket.repository.write.CustomerOrderRepository;
import by.cryptic.springmarket.service.command.OrderCancelCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderCancelCommandHandler implements CommandHandler<OrderCancelCommand> {

    private final CustomerOrderRepository customerOrderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomerOrderRepository orderRepository;
    private final AuthUtil authUtil;

    @Transactional
    @CacheEvict(key = "'order:' + #command.orderId()")
    public void handle(OrderCancelCommand command) {
        log.info("Handling order cancel command {}", command);
        AppUser user = authUtil.getUserFromContext();
        CustomerOrder order = customerOrderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Order not found with id : %s".formatted(command.orderId())));
        if (user.getOrders().contains(order) && order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            log.info("Order cancelled successfully");
        } else {
            log.error("Order cannot be cancelled");
            throw new IllegalStateException("Order not completed");
        }
        orderRepository.save(order);
        eventPublisher.publishEvent(new OrderCanceledEvent(order.getId(), user.getEmail()));
    }
}
