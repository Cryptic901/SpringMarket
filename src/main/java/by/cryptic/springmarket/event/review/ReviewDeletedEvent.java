package by.cryptic.springmarket.event.review;

import by.cryptic.springmarket.event.DomainEvent;
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
