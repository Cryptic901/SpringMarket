package by.cryptic.orderservice.service.command;


import java.util.UUID;

public record OrderUpdateCommand(UUID orderId, String location, UUID userId, String email) {
}
