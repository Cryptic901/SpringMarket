package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.FullOrderDTO;
import by.cryptic.springmarket.dto.OrderDTO;
import by.cryptic.springmarket.dto.OrderResponse;
import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.mapper.FullOrderMapper;
import by.cryptic.springmarket.mapper.OrderMapper;
import by.cryptic.springmarket.mapper.ProductMapper;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.model.CustomerOrder;
import by.cryptic.springmarket.model.Product;
import by.cryptic.springmarket.repository.CustomerOrderRepository;
import by.cryptic.springmarket.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final FullOrderMapper fullOrderMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CustomerOrderRepository customerOrderRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }

    @Cacheable(key = "#id")
    public FullOrderDTO getOrderById(UUID id) {
        return fullOrderMapper.toDto(orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not fount with id : %s".formatted(id))));
    }

    @Transactional
    @CachePut(key = "#dto.productId + '-' + #dto.location + '-' + #dto.status + '-' + #dto.paymentMethod")
    public OrderResponse createOrder(OrderDTO dto, AppUser appUser) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id : %s".formatted(dto.productId())));
        FullOrderDTO fullOrderDTO = FullOrderDTO.builder()
                .product(productMapper.toDto(product))
                .location(dto.location())
                .paymentMethod(dto.paymentMethod())
                .status(OrderStatus.IN_PROGRESS)
                .build();
        List<CustomerOrder> orders = appUser.getOrders();
        orders.add(fullOrderMapper.toEntity(fullOrderDTO));
        orderRepository.save(fullOrderMapper.toEntity(fullOrderDTO));
        return new OrderResponse(OrderStatus.IN_PROGRESS);
    }

    @Transactional
    @CachePut(key = "#id")
    public FullOrderDTO updateOrder(UUID id, OrderDTO updateDTO, AppUser appUser) {
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(id)));
        if (appUser.getOrders().contains(order)) {
            orderMapper.updateEntity(order, updateDTO);
        }
        return fullOrderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @CacheEvict(key = "#id")
    public OrderResponse cancelOrder(UUID id, AppUser appUser) {
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(id)));
        if (appUser.getOrders().contains(order)) {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }
        orderRepository.save(order);
        return new OrderResponse(OrderStatus.CANCELLED);
    }
}
