package by.cryptic.springmarket.service.query.handler.review;

import by.cryptic.springmarket.dto.ReviewDTO;
import by.cryptic.springmarket.mapper.FullReviewMapper;
import by.cryptic.springmarket.repository.read.ReviewViewRepository;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewGetByIdQueryHandler implements QueryHandler<UUID, ReviewDTO> {

    private final FullReviewMapper fullReviewMapper;
    private final ReviewViewRepository reviewViewRepository;

    @Cacheable(key = "'review:' + #id")
    public ReviewDTO handle(UUID id) {
        return fullReviewMapper.toDto(reviewViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: %s".formatted(id))));
    }
}
