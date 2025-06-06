package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.PaymentMethod;

public record ShortOrderDTO(PaymentMethod paymentMethod, String location) {
}
