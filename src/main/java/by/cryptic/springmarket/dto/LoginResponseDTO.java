package by.cryptic.springmarket.dto;

import java.time.Duration;

public record LoginResponseDTO(String token, Duration expires) {
}
