package by.cryptic.utils.event.review;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDeletedEvent extends DomainEvent implements ReviewEvent{
    private UUID reviewId;
}
