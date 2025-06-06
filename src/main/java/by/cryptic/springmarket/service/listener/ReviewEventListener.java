package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.enums.EventType;
import by.cryptic.springmarket.event.review.ReviewCreatedEvent;
import by.cryptic.springmarket.event.review.ReviewDeletedEvent;
import by.cryptic.springmarket.event.review.ReviewUpdatedEvent;
import by.cryptic.springmarket.mapper.ReviewMapper;
import by.cryptic.springmarket.model.read.ReviewView;
import by.cryptic.springmarket.repository.read.ReviewViewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {
    private final ObjectMapper objectMapper;
    private final ReviewViewRepository reviewViewRepository;
    private final ReviewMapper reviewMapper;

    @KafkaListener(topics = "review-topic", groupId = "review-group")
    public void listenReviews(String rawEvent) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        switch (EventType.valueOf(type)) {
            case ReviewCreatedEvent -> {
                ReviewCreatedEvent event = objectMapper.treeToValue(node, ReviewCreatedEvent.class);
                reviewViewRepository.save(ReviewView.builder()
                        .createdBy(event.getCreatedBy())
                        .image(event.getImage())
                        .productId(event.getProductId())
                        .reviewId(event.getReviewId())
                        .title(event.getTitle())
                        .createdAt(event.getTimestamp())
                        .rating(event.getRating())
                        .description(event.getDescription())
                        .build());
            }
            case ReviewUpdatedEvent -> {
                ReviewUpdatedEvent event = objectMapper.treeToValue(node, ReviewUpdatedEvent.class);
                reviewViewRepository.findById(event.getReviewId()).ifPresent(reviewView -> {
                    reviewMapper.updateEvent(reviewView, event);
                    reviewViewRepository.save(reviewView);
                });
            }
            case ReviewDeletedEvent -> {
                ReviewDeletedEvent event = objectMapper.treeToValue(node, ReviewDeletedEvent.class);
                reviewViewRepository.findById(event.getReviewId()).ifPresent(reviewView ->
                        reviewViewRepository.deleteById(event.getReviewId()));
            }
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}
