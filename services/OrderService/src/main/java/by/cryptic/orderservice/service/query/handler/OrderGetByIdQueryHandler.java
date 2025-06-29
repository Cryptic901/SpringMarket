package by.cryptic.orderservice.service.query.handler;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.mapper.FullOrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.orderservice.service.query.OrderGetByIdQuery;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderGetByIdQueryHandler implements QueryHandler<OrderGetByIdQuery, OrderDTO> {

    private final OrderViewRepository orderViewRepository;
    private final FullOrderMapper fullOrderMapper;

    @Cacheable(key = "'order:' + #query.orderId()")
    @Transactional(readOnly = true)
    public OrderDTO handle(OrderGetByIdQuery query) {
        CustomerOrderView customerOrder = orderViewRepository.findById(query.orderId())
                .stream()
                .filter(order -> order.getCreatedBy().equals(query.userId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(query.orderId())));
        return fullOrderMapper.toDto(customerOrder);
    }
}
