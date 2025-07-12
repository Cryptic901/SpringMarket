package by.cryptic.orderservice.service.command.handler;

import by.cryptic.orderservice.client.CartServiceClient;
import by.cryptic.orderservice.client.ProductServiceClient;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.OrderStatus;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OrderProduct;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import exceptions.EmptyCartException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderCreateCommandHandler implements CommandHandler<OrderCreateCommand> {
    private final CustomerOrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CartServiceClient cartServiceClient;
    private final CacheManager cacheManager;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public void handle(OrderCreateCommand command) {
        log.info("Creating order : {}", command);

        CustomerOrder order = CustomerOrder.builder()
                .orderStatus(OrderStatus.IN_PROGRESS)
                .location(command.location())
                .userId(command.userId())
                .products(new ArrayList<>())
                .build();
        List<OrderedProductDTO> productsToUpdate = new ArrayList<>();
        try {
            List<CartProductDTO> productsToOrder = cartServiceClient.getCartProducts(command.userId()).getBody();

            if (productsToOrder == null) {
                throw new EmptyCartException("Your cart is empty");
            }

            for (CartProductDTO cartProduct : productsToOrder) {
                OrderProduct orderProduct = OrderProduct.builder()
                        .order(order)
                        .productId(cartProduct.getProductId())
                        .quantity(cartProduct.getQuantity())
                        .build();

                ProductDTO productFromCart = productServiceClient.getProductById(cartProduct.getProductId()).getBody();

                if (productFromCart == null) {
                    throw new EntityNotFoundException("Product with id %s not found".formatted(cartProduct.getProductId()));
                }
                int remainingQuantity = productFromCart.quantity() - cartProduct.getQuantity();

                if (remainingQuantity < 0) {
                    throw new IllegalStateException("Not enough products");
                }

                productsToUpdate.add(new OrderedProductDTO(cartProduct.getProductId(), remainingQuantity));

                order.getProducts().add(orderProduct);
                order.setPrice(order.getPrice().add(
                        (cartProduct.getPricePerUnit()
                                .multiply(BigDecimal.valueOf(cartProduct.getQuantity())))));
            }

            orderRepository.save(order);
            cartServiceClient.removeAllItemsFromCart(command.userId());

            eventPublisher.publishEvent(OrderSuccessEvent.builder()
                    .orderId(order.getId())
                    .userEmail(command.userEmail())
                    .listOfProducts(productsToUpdate)
                    .createdTimestamp(order.getCreatedAt())
                    .createdBy(command.userId())
                    .location(order.getLocation())
                    .createdTimestamp(order.getCreatedAt())
                    .orderStatus(order.getOrderStatus())
                    .price(order.getPrice())
                    .build());
        } catch (Exception e) {
            log.error("Order failed {}", e.getMessage());
            eventPublisher.publishEvent(OrderFailedEvent.builder()
                    .orderId(order.getId())
                    .userEmail(command.userEmail())
                    .listOfProducts(productsToUpdate)
                    .createdTimestamp(order.getCreatedAt())
                    .createdBy(command.userId())
                    .location(order.getLocation())
                    .createdTimestamp(order.getCreatedAt())
                    .orderStatus(order.getOrderStatus())
                    .price(order.getPrice())
                    .build());
        }
        Objects.requireNonNull(cacheManager.getCache("orders"))
                .put("order:" + command.location() + '-', orderMapper.toDto(order));
    }
}
