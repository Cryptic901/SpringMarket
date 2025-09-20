package by.cryptic.orderservice.service.command;

import by.cryptic.utils.enums.PaymentMethod;

public record OrderCreateDTO(String location, PaymentMethod paymentMethod) {
}
