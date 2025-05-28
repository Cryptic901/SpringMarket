package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.*;
import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.event.OrderEvent;
import by.cryptic.springmarket.mapper.FullOrderMapper;
import by.cryptic.springmarket.mapper.OrderMapper;
import by.cryptic.springmarket.mapper.ProductMapper;
import by.cryptic.springmarket.model.*;
import by.cryptic.springmarket.repository.AppUserRepository;
import by.cryptic.springmarket.repository.CartProductRepository;
import by.cryptic.springmarket.repository.CustomerOrderRepository;
import by.cryptic.springmarket.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final FullOrderMapper fullOrderMapper;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final ProductMapper productMapper;
    private final CartProductRepository cartProductRepository;
    private final CartService cartService;
    private final ApplicationEventPublisher eventPublisher;
    private final AppUserRepository appUserRepository;

    public List<ResponseOrderDTO> getAllOrders(AppUser user) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getCreatedBy().equals(user.getId()))
                .map(orderMapper::toResponseDto).toList();
    }

    @Cacheable(key = "'order:' + #id")
    @Transactional(readOnly = true)
    public FullOrderDTO getOrderById(UUID id, AppUser user) {
        CustomerOrder customerOrder = orderRepository.findById(id)
                .stream()
                .filter(order -> order.getCreatedBy().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(id)));
        List<ProductDTO> products = customerOrder.getProducts().stream()
                .map(OrderProduct::getProduct).map(productMapper::toDto).toList();
        FullOrderDTO fullOrderDTO = fullOrderMapper.toDto(customerOrder);
        fullOrderDTO.setStatus(customerOrder.getOrderStatus());
        fullOrderDTO.setProducts(products);
        return fullOrderDTO;
    }

    @Transactional
    @CachePut(key = "'order:' + #appUser.id + ':' + #dto.location + ':' + #dto.paymentMethod")
    public OrderResponse createOrder(OrderDTO dto, AppUser appUser) {
        log.info("Creating order : {}", dto);
        AppUser user = appUserRepository.findByIdWithCart(appUser.getId());
        List<CartProduct> productsToOrder = cartProductRepository.findAllByIdWithProducts(user.getCart().getItems()
                .stream().map(CartProduct::getId).toList());
        CustomerOrder order = CustomerOrder.builder()
                .orderStatus(OrderStatus.IN_PROGRESS)
                .paymentMethod(dto.paymentMethod())
                .location(dto.location())
                .appUser(user)
                .products(new ArrayList<>())
                .build();
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
        cartService.deleteAllProductsFromCart(user);

        eventPublisher.publishEvent(OrderEvent.builder()
                .orderId(order.getId())
                .userEmail(user.getEmail())
                .status(OrderStatus.IN_PROGRESS)
                .paymentMethod(dto.paymentMethod())
                .createdAt(order.getCreatedAt())
                .build());
        return new OrderResponse(OrderStatus.IN_PROGRESS);
    }

    @Async("orderExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrder(OrderEvent event) {
        try {
            log.info("Sending order event : {}", event);
            kafkaTemplate.send("order-topic", event);
        } catch (Exception e) {
            log.error("Failed to send notification message {}", event.getOrderId(), e);
        }
    }

        @Transactional
        @CachePut(key = "'order:' + #id")
        public FullOrderDTO updateOrder (UUID id, OrderDTO updateDTO, AppUser user) {
            CustomerOrder order = customerOrderRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(id)));
            if (user.getOrders().contains(order) && order.getOrderStatus().equals(OrderStatus.IN_PROGRESS)) {
                orderMapper.updateEntity(order, updateDTO);
            } else {
                throw new IllegalArgumentException("Order should be in progress to update");
            }
            eventPublisher.publishEvent(OrderEvent.builder()
                    .orderId(order.getId())
                    .userEmail(user.getEmail())
                    .status(OrderStatus.IN_PROGRESS)
                    .paymentMethod(updateDTO.paymentMethod())
                    .createdAt(order.getCreatedAt())
                    .build());
            return fullOrderMapper.toDto(orderRepository.save(order));
        }

        @Transactional
        @CacheEvict(key = "'order:' + #id")
        public OrderResponse cancelOrder (UUID id, AppUser user){
            CustomerOrder order = customerOrderRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(id)));
            if (user.getOrders().contains(order) && order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                order.setOrderStatus(OrderStatus.CANCELLED);
            } else {
                throw new IllegalArgumentException("Order not completed");
            }
            orderRepository.save(order);
            eventPublisher.publishEvent(OrderEvent.builder()
                    .orderId(order.getId())
                    .userEmail(user.getEmail())
                    .status(OrderStatus.CANCELLED)
                    .paymentMethod(order.getPaymentMethod())
                    .createdAt(order.getCreatedAt())
                    .build());
            return new OrderResponse(OrderStatus.CANCELLED);
        }
    }
