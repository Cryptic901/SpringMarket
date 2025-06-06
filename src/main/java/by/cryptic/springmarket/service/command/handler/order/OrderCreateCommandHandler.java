package by.cryptic.springmarket.service.command.handler.order;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.event.order.OrderCreatedEvent;
import by.cryptic.springmarket.model.write.*;
import by.cryptic.springmarket.repository.write.CartProductRepository;
import by.cryptic.springmarket.repository.write.CustomerOrderRepository;
import by.cryptic.springmarket.repository.write.ProductRepository;
import by.cryptic.springmarket.service.command.OrderCreateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import by.cryptic.springmarket.util.CartUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderCreateCommandHandler implements CommandHandler<OrderCreateCommand> {
    private final CartProductRepository cartProductRepository;
    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthUtil authUtil;
    private final CartUtil cartUtil;

    @CachePut(key = "'order:' + #command.location + ':' + #command.paymentMethod")
    @Transactional
    public void handle(OrderCreateCommand command) {
        log.info("Creating order : {}", command);
        AppUser user = authUtil.getUserFromContext();

        CustomerOrder order = CustomerOrder.builder()
                .orderStatus(OrderStatus.IN_PROGRESS)
                .paymentMethod(command.paymentMethod())
                .location(command.location())
                .appUser(user)
                .products(new ArrayList<>())
                .build();

        transferCartToOrder(order, user);

        eventPublisher.publishEvent(OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userEmail(user.getEmail())
                .createdTimestamp(order.getCreatedAt())
                .paymentMethod(order.getPaymentMethod())
                .createdBy(user.getId())
                .location(order.getLocation())
                .createdTimestamp(order.getCreatedAt())
                .build());
    }

    private void transferCartToOrder(CustomerOrder order, AppUser user) {
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
