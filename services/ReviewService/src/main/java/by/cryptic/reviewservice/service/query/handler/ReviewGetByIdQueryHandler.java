package by.cryptic.reviewservice.service.query.handler;

import by.cryptic.utils.DTO.ReviewDTO;
import by.cryptic.reviewservice.mapper.FullReviewMapper;
import by.cryptic.reviewservice.repository.read.ReviewViewRepository;
import by.cryptic.utils.QueryHandler;
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
