package by.cryptic.orderservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.EmptyCartException;
import by.cryptic.exceptions.NotEnoughProductsException;
import by.cryptic.orderservice.client.CartServiceClient;
import by.cryptic.orderservice.client.ProductServiceClient;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OrderProduct;
import by.cryptic.orderservice.publisher.OrderEventPublisher;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.event.cart.CartClearedBySagaEvent;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCreateCommandHandler implements CommandHandler<OrderCreateCommand> {

    private final CustomerOrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final CacheManager cacheManager;
    private final ProductServiceClient productServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    @Transactional
    public void handle(OrderCreateCommand command) {
        log.info("Creating order : {}", command);
        List<OrderedProductDTO> productsToUpdate = new ArrayList<>();
        CustomerOrder order = new CustomerOrder();
        try {
            List<CartProductDTO> productsToOrder = getListOfCartProductsByFeignClient(command.userId());
            if (productsToOrder == null || productsToOrder.isEmpty()) {
                throw new EmptyCartException("Your cart is empty");
            }

            order = createOrderAndValidate(command, productsToUpdate, productsToOrder);

            saveOrderWithCircuitBreaker(order, command);

            orderEventPublisher.sentOrderCreatedEvent(order, command, productsToUpdate);

            log.info("-- Order created successfully: {}", order);
            log.info("-- Products to order : {}", productsToOrder);
            log.info("-- Products to update : {}", productsToUpdate);

        } catch (EmptyCartException | EntityNotFoundException e) {
            log.error("Error while creating order", e);
            orderEventPublisher.sentOrderFailedEventWithException(order, productsToUpdate, e);
            throw e;
        } catch (Exception e) {
            log.error("Technical error when creating order", e);
            orderEventPublisher.sentOrderFailedEventWithException(order, productsToUpdate, e);
            throw new CreatingException("Error while creating order", e);
        }
    }

    private CustomerOrder createOrderAndValidate(OrderCreateCommand command,
                                                 List<OrderedProductDTO> productsToUpdate,
                                                 List<CartProductDTO> productsToOrder) {
        CustomerOrder order = CustomerOrder.builder()
                .orderStatus(OrderStatus.PENDING)
                .location(command.location())
                .userId(command.userId())
                .userEmail(command.userEmail())
                .products(new ArrayList<>())
                .price(BigDecimal.ZERO)
                .build();

        for (CartProductDTO cartProduct : productsToOrder) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .productId(cartProduct.getProductId())
                    .quantity(cartProduct.getQuantity())
                    .build();

            ProductDTO productFromCart = getProductByFeignClient(cartProduct.getProductId());

            if (productFromCart == null) {
                throw new EntityNotFoundException("Product with id %s not found".formatted(cartProduct.getProductId()));
            }

            int remainingQuantity = productFromCart.quantity() - cartProduct.getQuantity();

            log.info(" -- Remaining quantity {}", remainingQuantity);
            if (remainingQuantity < 0) {
                throw new NotEnoughProductsException("Not enough products");
            }
            productsToUpdate.add(new OrderedProductDTO(cartProduct.getProductId(), cartProduct.getQuantity()));

            order.getProducts().add(orderProduct);
            order.setPrice(order.getPrice().add(
                    (productFromCart.price()
                            .multiply(BigDecimal.valueOf(cartProduct.getQuantity())))));
        }
        log.info("-- Products to update: {}", productsToUpdate);
        return order;
    }

    @CircuitBreaker(name = "orderCircuitBreaker", fallbackMethod = "orderCreateCircuitBreakerFallback")
    public void saveOrderWithCircuitBreaker(CustomerOrder order, OrderCreateCommand command) {
        orderRepository.save(order);

        removeAllItemsFromCartByFeignClient(command.userId(), CartClearedBySagaEvent.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .userEmail(order.getUserEmail())
                .build());

        updateCache(order);
    }

    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productClientCircuitBreakerFallback")
    public ProductDTO getProductByFeignClient(UUID productId) {
        return productServiceClient.getProductById(productId).getBody();
    }

    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "cartClientGetListOfCartProductsCircuitBreakerFallback")
    public List<CartProductDTO> getListOfCartProductsByFeignClient(UUID userId) {
        return cartServiceClient.getCartProductsByUserId(userId).getBody();
    }

    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "cartClientRemoveAllItemsFromCartCircuitBreakerFallback")
    public void removeAllItemsFromCartByFeignClient(UUID userId, CartClearedBySagaEvent event) {
        cartServiceClient.removeAllItemsFromCartByUserId(userId);
        orderEventPublisher.sentCartClearedEvent(event);
    }

    private void updateCache(CustomerOrder order) {
        try {
            Objects.requireNonNull(cacheManager.getCache("orders"))
                    .put("order:" + order.getId(), order);
        } catch (Exception e) {
            log.warn("Failed to update cache {}", e.getMessage());
        }
    }

    public void orderCreateCircuitBreakerFallback(CustomerOrder order, OrderCreateCommand orderCreateCommand, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", orderCreateCommand.toString(), t.getMessage(), t);
        throw new CreatingException("Failed to create order:" + orderCreateCommand, t);
    }

    public void cartClientGetListOfCartProductsCircuitBreakerFallback(Throwable t) {
        log.error("Failed to create order after all retry attempts. Cause: {}", t.getMessage(), t);
        throw new CreatingException("Failed to create order", t);
    }

    public void cartClientRemoveAllItemsFromCartCircuitBreakerFallback(OrderCreateCommand orderCreateCommand, CartClearedBySagaEvent cartClearedBySagaEvent, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", orderCreateCommand.toString(), t.getMessage(), t);
        throw new CreatingException("Failed to create order:" + orderCreateCommand, t);
    }

    public void productClientCircuitBreakerFallback(UUID productId, Throwable t) {
        log.error("Failed to create order with product id {} after all retry attempts. Cause: {}", productId, t.getMessage(), t);
        throw new CreatingException("Failed to create order with productId:" + productId, t);
    }
}
