package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.CreateReviewDTO;
import by.cryptic.springmarket.dto.FullReviewDTO;
import by.cryptic.springmarket.dto.ReviewDTO;
import by.cryptic.springmarket.dto.UpdateReviewDTO;
import by.cryptic.springmarket.mapper.FullReviewMapper;
import by.cryptic.springmarket.mapper.ProductMapper;
import by.cryptic.springmarket.mapper.ReviewMapper;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.model.Product;
import by.cryptic.springmarket.model.Review;
import by.cryptic.springmarket.repository.ProductRepository;
import by.cryptic.springmarket.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"reviews"})
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final FullReviewMapper fullReviewMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ReviewDTO> getAllReviews(UUID productId) {
        return reviewRepository.findAll().stream()
                .filter(rev -> rev.getProductId().equals(productId))
                .map(reviewMapper::toDto).toList();
    }
    @Cacheable(key = "#id")
    public FullReviewDTO getReviewById(UUID id) {
        return fullReviewMapper.toDto(reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: %s".formatted(id))));
    }

    @Transactional
    @CachePut(key = "#result.title + '-' + #result.description")
    public FullReviewDTO createReview(CreateReviewDTO dto, AppUser appUser) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: %s".formatted(dto.productId())));
        FullReviewDTO reviewDTO = FullReviewDTO.builder()
                .title(dto.title())
                .rating(dto.rating())
                .description(dto.description())
                .image(dto.image())
                .product(productMapper.toDto(product))
                .build();
        appUser.getReviews().add(fullReviewMapper.toEntity(reviewDTO));
        return reviewDTO;
    }

    @Transactional
    @CachePut(key = "#id")
    public FullReviewDTO updateReview(UUID id, UpdateReviewDTO dto, AppUser appUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: %s".formatted(id)));
        if (appUser.getReviews().contains(review)) {
            reviewMapper.updateEntity(review, dto);
        }
        return fullReviewMapper.toDto(reviewRepository.save(review));
    }

    @Transactional
    @CacheEvict(key = "#id")
    public void deleteReview(UUID id, AppUser appUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: %s".formatted(id)));
        if (appUser.getReviews().contains(review)) {
            reviewRepository.deleteById(id);
        }
    }
}
