package by.cryptic.utils.event.review;

import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreatedEvent extends DomainEvent implements ReviewEvent {
    private UUID reviewId;
    private UUID productId;
    private String title;
    private String description;
    private Double rating;
    private String image;
    private UUID createdBy;
    private static final String version = "1.0";
    @Builder.Default
    private String source = ReviewCreatedEvent.class.getName();

}
