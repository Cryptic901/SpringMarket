package by.cryptic.cartservice.service.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartDeleteProductCommand(@NotNull UUID productId, @NotNull UUID userId) {
}
