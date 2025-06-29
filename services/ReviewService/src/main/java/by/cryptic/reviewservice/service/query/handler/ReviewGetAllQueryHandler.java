package by.cryptic.reviewservice.service.query.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.repository.read.ReviewViewRepository;
import by.cryptic.reviewservice.service.query.ReviewQuery;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewGetAllQueryHandler implements QueryHandler<UUID, List<ReviewQuery>> {

    private final ReviewViewRepository reviewViewRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewQuery> handle(UUID productId) {
        return reviewViewRepository.findAll().stream()
                .filter(rev -> rev.getProductId().equals(productId))
                .map(reviewMapper::toQuery).toList();
    }
}
