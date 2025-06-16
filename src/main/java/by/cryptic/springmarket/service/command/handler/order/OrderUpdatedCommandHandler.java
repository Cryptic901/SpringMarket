package by.cryptic.springmarket.service.command.handler.order;

import by.cryptic.springmarket.dto.ShortOrderDTO;
import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.event.order.OrderUpdatedEvent;
import by.cryptic.springmarket.mapper.OrderMapper;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.CustomerOrder;
import by.cryptic.springmarket.repository.write.CustomerOrderRepository;
import by.cryptic.springmarket.service.command.OrderUpdateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
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
    private final AuthUtil authUtil;
    private final CacheManager cacheManager;

    @Transactional
    public void handle(OrderUpdateCommand command) {
        AppUser user = authUtil.getUserFromContext();
        CustomerOrder order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Order not found with id : %s".formatted(command.orderId())));
        if (user.getOrders().contains(order) && order.getOrderStatus().equals(OrderStatus.IN_PROGRESS)) {
            orderMapper.updateEntity(order, new ShortOrderDTO(command.paymentMethod(), command.location()));
        } else {
            throw new IllegalArgumentException("Order should be in progress to update");
        }
        eventPublisher.publishEvent(OrderUpdatedEvent.builder()
                .orderId(order.getId())
                .userEmail(user.getEmail())
                .status(OrderStatus.IN_PROGRESS)
                .paymentMethod(command.paymentMethod())
                .updatedTimestamp(order.getUpdatedAt())
                .build());
        Objects.requireNonNull(cacheManager.getCache("orders"))
                .put("order:" + command.location() + '-' + command.paymentMethod(), order);
    }
}
