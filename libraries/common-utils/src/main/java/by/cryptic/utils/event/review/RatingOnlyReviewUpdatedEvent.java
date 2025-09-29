package by.cryptic.utils.event.review;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingOnlyReviewUpdatedEvent extends DomainEvent implements ReviewEvent {
    private UUID reviewId;
    private UUID productId;
    private Double rating;
    private static final String version = "1.0";
    @Builder.Default
    private String source = RatingOnlyReviewUpdatedEvent.class.getName();
}
