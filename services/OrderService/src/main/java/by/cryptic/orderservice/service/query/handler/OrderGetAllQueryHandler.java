package by.cryptic.orderservice.service.query.handler;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.orderservice.service.query.OrderGetAllQuery;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderGetAllQueryHandler implements QueryHandler<OrderGetAllQuery, List<OrderDTO>> {


    private final OrderViewRepository orderViewRepository;
    private final OrderMapper orderMapper;

    public List<OrderDTO> handle(OrderGetAllQuery orderGetAllQuery) {
        return orderViewRepository.findAll().stream()
                .filter(order -> order.getCreatedBy().equals(orderGetAllQuery.id()))
                .map(orderMapper::viewToDto).toList();
    }
}
