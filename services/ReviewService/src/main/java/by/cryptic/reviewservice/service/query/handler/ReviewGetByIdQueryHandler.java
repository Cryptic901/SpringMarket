package by.cryptic.reviewservice.service.query.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.utils.DTO.ReviewDTO;
import by.cryptic.utils.QueryHandler;
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
public class ReviewGetByIdQueryHandler implements QueryHandler<UUID, ReviewDTO> {

    private final ReviewRepository reviewRepository;
    private final CacheManager cacheManager;
    private final ReviewMapper reviewMapper;

    public ReviewDTO handle(UUID id) {
        return findInCacheOrDB(id);
    }

    public ReviewDTO findInCacheOrDB(UUID id) {
        String cacheKey = "review:" + id;
        Cache cache = cacheManager.getCache("reviews");
        if (cache != null) {
            Review cachedReview = cache.get(cacheKey, Review.class);
            if (cachedReview != null) {
                log.debug("Review was found in cache {}", cachedReview);
                return reviewMapper.toDto(cachedReview);
            }
        }

        Review dbReview = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review with id %s was not found"
                        .formatted(id)));
        if (cache != null) {
            cache.put(cacheKey, dbReview);
        }
        return reviewMapper.toDto(dbReview);
    }
}
