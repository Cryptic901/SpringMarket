package by.cryptic.utils.event.review;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingOnlyReviewCreatedEvent extends DomainEvent implements ReviewEvent {
    private UUID reviewId;
    private UUID productId;
    private Double rating;
    private UUID createdBy;
    private static final String version = "1.0";
    @Builder.Default
    private String source = RatingOnlyReviewCreatedEvent.class.getName();
}
