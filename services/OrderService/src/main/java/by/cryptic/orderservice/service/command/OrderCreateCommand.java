package by.cryptic.orderservice.service.command;

import java.util.UUID;

public record OrderCreateCommand(String location, UUID userId, String userEmail) {
}
