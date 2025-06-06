package by.cryptic.springmarket.event.category;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryUpdatedEvent extends DomainEvent implements CategoryEvent {
    private UUID categoryId;
    private String name;
    private String description;
}
