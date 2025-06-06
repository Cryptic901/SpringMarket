package by.cryptic.springmarket.service.command;

import by.cryptic.springmarket.enums.PaymentMethod;

public record OrderCreateCommand(PaymentMethod paymentMethod, String location) {
}
