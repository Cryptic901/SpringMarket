package by.cryptic.springmarket.service.query.handler.cart;

import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Cart;
import by.cryptic.springmarket.repository.read.CartViewRepository;
import by.cryptic.springmarket.service.query.CartGetAllQuery;
import by.cryptic.springmarket.service.query.CartProductDTO;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import by.cryptic.springmarket.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartGetAllQueryHandler implements QueryHandler<CartGetAllQuery, List<CartProductDTO>> {

    private final AuthUtil authUtil;
    private final CartViewRepository cartViewRepository;

    @Transactional(readOnly = true)
    public List<CartProductDTO> handle(CartGetAllQuery cartGetAllQuery) {
        AppUser user = authUtil.getUserFromContext();
        return cartViewRepository.findByUserIdWithItems(user.getId())
                .map(Cart::getItems)
                .orElse(Collections.emptyList())
                .stream().map(pr ->
                        new CartProductDTO(pr.getProduct().getId(), pr.getQuantity(), pr.getPricePerUnit()))
                .toList();
    }
}
