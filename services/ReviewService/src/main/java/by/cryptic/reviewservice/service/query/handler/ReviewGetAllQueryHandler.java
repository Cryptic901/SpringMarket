package by.cryptic.reviewservice.service.query.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.repository.read.ReviewViewRepository;
import by.cryptic.utils.DTO.ReviewDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
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

    @Override
    public List<ReviewDTO> handle(UUID productId) {
        List<ReviewDTO> result = reviewViewRepository.findAll().stream()
                .filter(rev -> rev.getProductId().equals(productId))
                .map(ReviewMapper::toDto).toList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("There are no reviews, you can be first!");
        }
        return result;
    }
}
