package by.cryptic.springmarket.service.query;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDTO(String title, Double rating,
                        String description, String image,
                        UUID creator, LocalDateTime createdAt) {
}
