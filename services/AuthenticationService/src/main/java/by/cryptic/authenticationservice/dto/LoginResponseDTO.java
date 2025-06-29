package by.cryptic.authenticationservice.dto;

import java.time.Duration;

public record LoginResponseDTO(String token, Duration expires) {
}
