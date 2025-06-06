package by.cryptic.springmarket.service.query.handler.order;

import by.cryptic.springmarket.dto.FullOrderDTO;
import by.cryptic.springmarket.mapper.FullOrderMapper;
import by.cryptic.springmarket.model.read.CustomerOrderView;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.repository.read.OrderViewRepository;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import by.cryptic.springmarket.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"orders"})
public class OrderGetByIdQueryHandler implements QueryHandler<UUID, FullOrderDTO> {

    private final OrderViewRepository orderViewRepository;
    private final AuthUtil authUtil;
    private final FullOrderMapper fullOrderMapper;

    @Cacheable(key = "'order:' + #id")
    @Transactional(readOnly = true)
    public FullOrderDTO handle(UUID id) {
        AppUser user = authUtil.getUserFromContext();
        CustomerOrderView customerOrder = orderViewRepository.findById(id)
                .stream()
                .filter(order -> order.getCreatedBy().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id : %s".formatted(id)));
        return fullOrderMapper.toDto(customerOrder);
    }
}
