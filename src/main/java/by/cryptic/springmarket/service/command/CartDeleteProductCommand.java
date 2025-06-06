package by.cryptic.springmarket.service.command;

import java.util.UUID;

public record CartDeleteProductCommand(UUID productId) {
}
