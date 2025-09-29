package by.cryptic.reviewservice.service.query.handler;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.repository.read.RatingOnlyReviewViewRepository;
import by.cryptic.reviewservice.service.query.ReviewGetAllQuery;
import by.cryptic.utils.DTO.RatingOnlyReviewDTO;
import by.cryptic.utils.handler.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class RatingOnlyReviewGetAllQueryHandler implements QueryHandler<ReviewGetAllQuery, List<RatingOnlyReviewDTO>> {

    private final RatingOnlyReviewViewRepository reviewViewRepository;

    @Override
    public List<RatingOnlyReviewDTO> handle(ReviewGetAllQuery query) {
        List<RatingOnlyReviewDTO> result = reviewViewRepository.findAll().stream()
                .filter(rev -> rev.getProductId().equals(query.productId()))
                .map(ReviewMapper::toDto).toList();

        if (result.isEmpty()) {
            throw new EntityNotFoundException("There are no reviews, you can be first!");
        }
        return result;
    }
}
