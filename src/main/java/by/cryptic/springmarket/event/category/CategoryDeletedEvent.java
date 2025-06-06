package by.cryptic.springmarket.event.category;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDeletedEvent extends DomainEvent implements CategoryEvent {
    private UUID categoryId;
}
