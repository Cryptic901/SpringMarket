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
public class ReviewUpdatedEvent extends DomainEvent implements ReviewEvent {
    private UUID reviewId;
    private UUID productId;
    private String title;
    private String description;
    private Double rating;
    private String image;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    private static final String version = "1.0";
    @Builder.Default
    private String source = ReviewUpdatedEvent.class.getName();
}
