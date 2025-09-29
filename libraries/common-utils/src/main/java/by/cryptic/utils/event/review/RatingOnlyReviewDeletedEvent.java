package by.cryptic.utils.event.review;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingOnlyReviewDeletedEvent extends DomainEvent implements ReviewEvent {
    private UUID reviewId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = RatingOnlyReviewDeletedEvent.class.getName();
}
