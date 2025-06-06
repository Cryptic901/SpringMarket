package by.cryptic.springmarket.event.review;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdatedEvent extends DomainEvent implements ReviewEvent {
    private UUID reviewId;
    private UUID productId;
    private String title;
    private String description;
    private Double rating;
    private String image;
    private UUID updatedBy;
}
