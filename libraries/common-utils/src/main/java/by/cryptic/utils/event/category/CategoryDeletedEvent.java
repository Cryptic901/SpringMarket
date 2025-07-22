package by.cryptic.utils.event.category;

import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDeletedEvent extends DomainEvent implements CategoryEvent {
    private UUID categoryId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CategoryDeletedEvent.class.getName();

    public CategoryDeletedEvent(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
