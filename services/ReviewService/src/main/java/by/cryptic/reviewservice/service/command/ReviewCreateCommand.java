package by.cryptic.reviewservice.service.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewCreateCommand(@NotBlank(message = "Title should not be null")
                                  String title,
                                  @NotBlank(message = "Description should not be null")
                                  String description,
                                  @NotNull(message = "Rating should not be null")
                                  Double rating,
                                  @NotBlank(message = "Image should not be null")
                                  String image,
                                  @NotNull(message = "Product id should not be null")
                                  UUID productId,
                                  UUID userId) {
}
