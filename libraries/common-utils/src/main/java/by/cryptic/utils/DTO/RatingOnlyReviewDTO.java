package by.cryptic.utils.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingOnlyReviewDTO {
    private UUID productId;
    private Double rating;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
