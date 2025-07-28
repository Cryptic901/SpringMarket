package by.cryptic.cartservice.service.query.handler;

import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.repository.read.CartViewRepository;
import by.cryptic.cartservice.service.query.CartGetAllQuery;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartGetAllQueryHandler implements QueryHandler<CartGetAllQuery, List<CartProductDTO>> {

    private final CartViewRepository cartViewRepository;

    @Transactional(readOnly = true)
    public List<CartProductDTO> handle(CartGetAllQuery cartGetAllQuery) {
        CartView cartView = cartViewRepository.findCartViewByUserId(cartGetAllQuery.userId())
                .orElseThrow(() -> new RuntimeException("User with id %s not found"
                        .formatted(cartGetAllQuery.userId())));
        return cartView.getProducts().stream().map(pr ->
                        new CartProductDTO(pr.getProductId(), pr.getQuantity(), pr.getPrice()))
                .toList();
    }
}
