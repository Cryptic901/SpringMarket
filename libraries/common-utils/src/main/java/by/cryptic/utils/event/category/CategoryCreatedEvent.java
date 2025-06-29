package by.cryptic.utils.event.category;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryCreatedEvent extends DomainEvent implements CategoryEvent {
    private UUID categoryId;
    private String name;
    private String description;
}
