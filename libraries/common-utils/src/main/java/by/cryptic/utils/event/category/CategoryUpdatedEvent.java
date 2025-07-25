package by.cryptic.utils.event.category;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
    private static final String version = "1.0";
    @Builder.Default
    private String source = CategoryUpdatedEvent.class.getName();
}
