package by.cryptic.orderservice.service.command;

import by.cryptic.exceptions.EmptyCartException;
import by.cryptic.exceptions.NotEnoughProductsException;
import by.cryptic.orderservice.client.CartServiceAdapter;
import by.cryptic.orderservice.client.CartServiceClient;
import by.cryptic.orderservice.client.ProductServiceAdapter;
import by.cryptic.orderservice.client.ProductServiceClient;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OrderProduct;
import by.cryptic.orderservice.publisher.OrderEventPublisher;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.enums.PaymentMethod;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(MockitoExtension.class)
class OrderCreateCommandHandlerTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @Mock
    private CartServiceClient cartServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private ProductServiceAdapter productServiceAdapter;

    @Mock
    private CartServiceAdapter cartServiceAdapter;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @InjectMocks
    private OrderCreateCommandHandler orderCreateCommandHandler;

    @Test
    void createOrder_whenProductsEnoughCartNotEmptyAndFieldsAreOk_shouldSaveOrder() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderProduct orderProduct = new OrderProduct();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatus.PENDING)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .userId(userId)
                .products(List.of(orderProduct))
                .price(BigDecimal.ZERO)
                .build();
        OrderCreateCommand orderCreateCommand = new OrderCreateCommand(order.getLocation().getY(),
                order.getLocation().getX(), PaymentMethod.CARD,
                userId, "user123@gmail.com", 5);
        List<CartProductDTO> cartProductDTOS = new ArrayList<>();
        cartProductDTOS.add(new CartProductDTO(UUID.randomUUID(), 1488, BigDecimal.ONE));
        Mockito.when(orderRepository.save(any(CustomerOrder.class))).thenReturn(order);
        Mockito.when(cartServiceAdapter.getListOfCartProductsByFeignClient(userId)).thenReturn(cartProductDTOS);
        Mockito.when(productServiceAdapter.getProductByFeignClient(any())).thenReturn
                (new ProductDTO("name", BigDecimal.ONE, 2000, "desc",
                        "image/url", UUID.randomUUID()));
        Mockito.when(cacheManager.getCache("orders")).thenReturn(cache);
        //Act
        orderCreateCommandHandler.handle(orderCreateCommand);
        //Assert
        Mockito.verify(cacheManager).getCache("orders");
        Mockito.verify(cache, Mockito.times(1)).put(startsWith("order:"), any());
        Mockito.verify(orderRepository, Mockito.times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void createOrder_whenProductsNotEnoughCartNotEmptyAndFieldsAreOk_shouldSaveOrder() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderProduct orderProduct = new OrderProduct();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatus.PENDING)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .userId(userId)
                .products(List.of(orderProduct))
                .price(BigDecimal.ZERO)
                .build();
        OrderCreateCommand orderCreateCommand = new OrderCreateCommand(order.getLocation().getY(),
                order.getLocation().getX(), PaymentMethod.CARD,
                userId, "user123@gmail.com", 5);
        List<CartProductDTO> cartProductDTOS = new ArrayList<>();
        cartProductDTOS.add(new CartProductDTO(UUID.randomUUID(), 2, BigDecimal.ONE));
        Mockito.when(cartServiceAdapter.getListOfCartProductsByFeignClient(userId)).thenReturn(cartProductDTOS);
        Mockito.when(productServiceAdapter.getProductByFeignClient(any())).thenReturn
                (new ProductDTO("name", BigDecimal.ONE, 1, "desc",
                        "image/url", UUID.randomUUID()));
        //Act
        //Assert
        Assert.assertThrows(NotEnoughProductsException.class, () -> orderCreateCommandHandler.handle(orderCreateCommand));
    }

    @Test
    void createOrder_whenProductsEnoughCartIsEmptyAndFieldsAreOk_shouldSaveOrder() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderProduct orderProduct = new OrderProduct();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatus.PENDING)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .userId(userId)
                .products(List.of(orderProduct))
                .price(BigDecimal.ZERO)
                .build();
        OrderCreateCommand orderCreateCommand = new OrderCreateCommand(order.getLocation().getY(),
                order.getLocation().getX(), PaymentMethod.CARD,
                userId, "user123@gmail.com", 5);
        List<CartProductDTO> cartProductDTOS = new ArrayList<>();
        Mockito.when(cartServiceAdapter.getListOfCartProductsByFeignClient(userId)).thenReturn(cartProductDTOS);
        //Act
        //Assert
        Assert.assertThrows(EmptyCartException.class, () -> orderCreateCommandHandler.handle(orderCreateCommand));
    }
}