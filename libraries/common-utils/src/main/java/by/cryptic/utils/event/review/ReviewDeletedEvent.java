package by.cryptic.utils.event.review;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.user.UserUpdatedEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDeletedEvent extends DomainEvent implements ReviewEvent{
    private UUID reviewId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = ReviewDeletedEvent.class.getName();

    public ReviewDeletedEvent(UUID reviewId) {
        this.reviewId = reviewId;
    }
}
