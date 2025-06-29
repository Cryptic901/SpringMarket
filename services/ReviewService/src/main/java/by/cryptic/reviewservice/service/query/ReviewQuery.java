package by.cryptic.reviewservice.service.query;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewQuery(String title, Double rating,
                          String description, String image,
                          UUID creator, LocalDateTime createdAt) {
}
