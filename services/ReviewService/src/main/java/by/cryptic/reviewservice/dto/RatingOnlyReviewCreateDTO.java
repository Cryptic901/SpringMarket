package by.cryptic.reviewservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingOnlyReviewCreateDTO {
    private UUID productId;
    @NotNull(message = "Rating should be not null")
    private Double rating;
}
