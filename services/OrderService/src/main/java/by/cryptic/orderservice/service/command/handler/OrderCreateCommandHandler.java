package by.cryptic.orderservice.service.command.handler;

import by.cryptic.utils.OrderStatus;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OrderProduct;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderCreateCommandHandler implements CommandHandler<OrderCreateCommand> {
//    private final CartProductRepository cartProductRepository;
    private final CustomerOrderRepository orderRepository;
//    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
//    private final CartUtil cartUtil;
    private final CacheManager cacheManager;
    private final OrderMapper orderMapper;

    @Transactional
    public void handle(OrderCreateCommand command) {
        log.info("Creating order : {}", command);

        CustomerOrder order = CustomerOrder.builder()
                .orderStatus(OrderStatus.IN_PROGRESS)
                .location(command.location())
                .userId(command.userId())
                .products(new ArrayList<>())
                .price(user.getCart().getTotal())
                .build();

        transferOrderToCart(order, user);

        eventPublisher.publishEvent(OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userEmail(command.userEmail())
                .createdTimestamp(order.getCreatedAt())
                .createdBy(command.userId())
                .location(order.getLocation())
                .createdTimestamp(order.getCreatedAt())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .build());
        Objects.requireNonNull(cacheManager.getCache("orders"))
                .put("order:" + command.location() + '-' , orderMapper.toDto(order));
    }

    private void transferOrderToCart(CustomerOrder order, AppUser user) {
        // TODO SAGA PATTERN
        List<CartProduct> productsToOrder = cartProductRepository.findAllByIdWithProducts(user.getCart().getItems()
                .stream().map(CartProduct::getId).toList());
        List<Product> productsToUpdate = new ArrayList<>();

        for (CartProduct cartProduct : productsToOrder) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .product(cartProduct.getProduct())
                    .quantity(cartProduct.getQuantity())
                    .build();

            order.getProducts().add(orderProduct);
            Product productFromCart = cartProduct.getProduct();
            productFromCart.setQuantity(productFromCart.getQuantity() - cartProduct.getQuantity());
            productsToUpdate.add(productFromCart);
        }
        orderRepository.save(order);
        productRepository.saveAll(productsToUpdate);
        cartUtil.clearCart(user);
    }
}
