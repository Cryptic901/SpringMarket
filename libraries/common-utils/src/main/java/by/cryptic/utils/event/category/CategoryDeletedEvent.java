package by.cryptic.utils.event.category;

import by.cryptic.utils.event.DomainEvent;
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
