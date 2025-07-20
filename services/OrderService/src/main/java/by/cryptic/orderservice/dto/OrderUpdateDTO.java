package by.cryptic.orderservice.dto;

import java.util.UUID;

public record OrderUpdateDTO(UUID orderId, String location) {
}
