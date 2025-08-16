package by.cryptic.cartservice.service.query.handler;

import by.cryptic.cartservice.mapper.CartMapper;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.repository.read.CartViewRepository;
import by.cryptic.cartservice.service.query.CartGetByIdQuery;
import by.cryptic.utils.DTO.CartProductDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartGetByIdQueryHandler implements QueryHandler<CartGetByIdQuery, CartProductDTO> {

    private final CartViewRepository cartViewRepository;

    @Override
    @Transactional(readOnly = true)
    public CartProductDTO handle(CartGetByIdQuery cartGetByIdQuery) {
        CartView cartView = cartViewRepository.findCartViewByUserId(cartGetByIdQuery.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with id %s not found"
                        .formatted(cartGetByIdQuery.userId())));
        return cartView.getProducts().stream().map(CartMapper::toDto)
                .filter(cp -> cp.getProductId().equals(cartGetByIdQuery.productId()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
}
