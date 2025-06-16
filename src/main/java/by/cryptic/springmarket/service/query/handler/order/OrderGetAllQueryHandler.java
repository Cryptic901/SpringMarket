package by.cryptic.springmarket.service.query.handler.order;

import by.cryptic.springmarket.dto.OrderDTO;
import by.cryptic.springmarket.mapper.OrderMapper;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.repository.read.OrderViewRepository;
import by.cryptic.springmarket.service.query.OrderGetAllQuery;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import by.cryptic.springmarket.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderGetAllQueryHandler implements QueryHandler<OrderGetAllQuery, List<OrderDTO>> {


    private final AuthUtil authUtil;
    private final OrderViewRepository orderViewRepository;
    private final OrderMapper orderMapper;

    public List<OrderDTO> handle(OrderGetAllQuery orderGetAllQuery) {
        AppUser user = authUtil.getUserFromContext();
        return orderViewRepository.findAll().stream()
                .filter(order -> order.getCreatedBy().equals(user.getId()))
                .map(orderMapper::toDto).toList();
    }
}
