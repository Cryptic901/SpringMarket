package by.cryptic.orderservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.EmptyCartException;
import by.cryptic.exceptions.NotEnoughProductsException;
import by.cryptic.orderservice.client.CartServiceAdapter;
import by.cryptic.orderservice.client.ProductServiceAdapter;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCreateCommandHandler implements CommandHandler<OrderCreateCommand> {

    private final CustomerOrderRepository orderRepository;
    private final CartServiceAdapter cartServiceAdapter;
    private final CacheManager cacheManager;
    private final ProductServiceAdapter productServiceAdapter;
    private final OrderEventPublisher orderEventPublisher;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional
    @CircuitBreaker(name = "orderCircuitBreaker", fallbackMethod = "orderCreateCircuitBreakerFallback")
    public void handle(OrderCreateCommand command) {
        log.info("Creating order : {}", command);
        List<OrderedProductDTO> productsToUpdate = new ArrayList<>();
        CustomerOrder order = new CustomerOrder();
        try {
            List<CartProductDTO> productsToOrder = cartServiceAdapter.getListOfCartProductsByFeignClient(command.userId());
            if (productsToOrder == null || productsToOrder.isEmpty()) {
                throw new EmptyCartException("Your cart is empty");
            }

            order = createOrderAndValidate(command, productsToUpdate, productsToOrder);

            saveOrder(order, command);

            orderEventPublisher.sentOrderCreatedEvent(order, command, productsToUpdate);

            log.info("-- Order created successfully: {}", order);
            log.info("-- Products to order : {}", productsToOrder);
            log.info("-- Products to update : {}", productsToUpdate);

        } catch (EmptyCartException | EntityNotFoundException | NotEnoughProductsException e) {
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
        Point point = geometryFactory.createPoint(new Coordinate(command.lon(), command.lat()));
        CustomerOrder order = CustomerOrder.builder()
                .orderStatus(OrderStatus.PENDING)
                .location(point)
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

            ProductDTO productFromCart = productServiceAdapter.getProductByFeignClient(cartProduct.getProductId());

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

    public void saveOrder(CustomerOrder order, OrderCreateCommand command) {
        orderRepository.save(order);

        cartServiceAdapter.removeAllItemsFromCartByFeignClient(command.userId());

        orderEventPublisher.sentCartClearedEvent(CartClearedBySagaEvent.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .userEmail(order.getUserEmail())
                .price(order.getPrice())
                .lat(command.lat())
                .lon(command.lon())
                .warehouseLimit(command.warehouseLimit())
                .build());

        updateCache(order);
    }

    private void updateCache(CustomerOrder order) {
        try {
            Objects.requireNonNull(cacheManager.getCache("orders"))
                    .put("order:" + order.getId(), order);
        } catch (Exception e) {
            log.warn("Failed to update cache {}", e.getMessage());
        }
    }

    public void orderCreateCircuitBreakerFallback(OrderCreateCommand orderCreateCommand, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", orderCreateCommand.toString(), t.getMessage(), t);
        throw new CreatingException("Failed to create order:" + orderCreateCommand, t);
    }
}
