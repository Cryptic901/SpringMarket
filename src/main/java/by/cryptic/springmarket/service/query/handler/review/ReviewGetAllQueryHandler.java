package by.cryptic.springmarket.service.query.handler.review;

import by.cryptic.springmarket.mapper.ReviewMapper;
import by.cryptic.springmarket.repository.read.ReviewViewRepository;
import by.cryptic.springmarket.service.query.ReviewDTO;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewGetAllQueryHandler implements QueryHandler<UUID, List<ReviewDTO>> {

    private final ReviewViewRepository reviewViewRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewDTO> handle(UUID productId) {
        return reviewViewRepository.findAll().stream()
                .filter(rev -> rev.getProductId().equals(productId))
                .map(reviewMapper::toDto).toList();
    }
}
