package by.cryptic.reviewservice.service.query.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.read.RatingOnlyReviewView;
import by.cryptic.reviewservice.model.write.RatingOnlyReview;
import by.cryptic.reviewservice.repository.read.RatingOnlyReviewViewRepository;
import by.cryptic.reviewservice.service.query.ReviewGetByIdQuery;
import by.cryptic.utils.DTO.RatingOnlyReviewDTO;
import by.cryptic.utils.handler.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"reviews"})
public class RatingOnlyReviewGetByIdQueryHandler implements QueryHandler<ReviewGetByIdQuery, RatingOnlyReviewDTO> {

    private final RatingOnlyReviewViewRepository reviewRepository;
    private final CacheManager cacheManager;

    @Override
    public RatingOnlyReviewDTO handle(ReviewGetByIdQuery query) {
        return findInCacheOrDB(query.productId());
    }

    public RatingOnlyReviewDTO findInCacheOrDB(UUID id) {
        String cacheKey = "review:" + id;
        Cache cache = cacheManager.getCache("reviews");
        if (cache != null) {
            RatingOnlyReview cachedReview = cache.get(cacheKey, RatingOnlyReview.class);
            if (cachedReview != null) {
                log.debug("Rating only review was found in cache {}", cachedReview);
                return ReviewMapper.toDto(cachedReview);
            }
        }

        RatingOnlyReviewView dbReview = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rating only review with id %s was not found"
                        .formatted(id)));
        if (cache != null) {
            cache.put(cacheKey, dbReview);
        }
        return ReviewMapper.toDto(dbReview);
    }
}
