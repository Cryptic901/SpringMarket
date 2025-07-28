package by.cryptic.reviewservice.listener;

import by.cryptic.reviewservice.mapper.ReviewMapper;
import by.cryptic.reviewservice.model.read.ReviewView;
import by.cryptic.reviewservice.repository.read.ReviewViewRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.review.ReviewCreatedEvent;
import by.cryptic.utils.event.review.ReviewDeletedEvent;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {
    private final ReviewViewRepository reviewViewRepository;
    private final ReviewMapper reviewMapper;

    @KafkaListener(topics = "review-topic", groupId = "review-group")
    public void listenReviews(DomainEvent event) {
        switch (event) {
            case ReviewCreatedEvent reviewCreatedEvent -> reviewViewRepository.save(ReviewView.builder()
                    .createdBy(reviewCreatedEvent.getCreatedBy())
                    .image(reviewCreatedEvent.getImage())
                    .productId(reviewCreatedEvent.getProductId())
                    .reviewId(reviewCreatedEvent.getReviewId())
                    .title(reviewCreatedEvent.getTitle())
                    .createdAt(reviewCreatedEvent.getTimestamp())
                    .rating(reviewCreatedEvent.getRating())
                    .description(reviewCreatedEvent.getDescription())
                    .build());

            case ReviewUpdatedEvent reviewUpdatedEvent ->
                    reviewViewRepository.findById(reviewUpdatedEvent.getReviewId()).ifPresent(reviewView -> {
                        reviewMapper.updateEvent(reviewView, reviewUpdatedEvent);
                        reviewViewRepository.save(reviewView);
                    });

            case ReviewDeletedEvent reviewDeletedEvent ->
                    reviewViewRepository.findById(reviewDeletedEvent.getReviewId()).ifPresent(reviewView ->
                            reviewViewRepository.deleteById(reviewDeletedEvent.getReviewId()));

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}