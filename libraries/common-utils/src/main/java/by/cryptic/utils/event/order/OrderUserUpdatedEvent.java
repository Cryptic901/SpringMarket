package by.cryptic.utils.event.order;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderUserUpdatedEvent {
    private UUID userId;
    @Email
    private String email;
    private static final String version = "1.0";
    @Builder.Default
    private String source = OrderUserUpdatedEvent.class.getName();
}
