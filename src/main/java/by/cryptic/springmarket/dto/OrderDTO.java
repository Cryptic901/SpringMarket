package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.PaymentMethod;
import by.cryptic.springmarket.model.Cart;

public record OrderDTO(PaymentMethod paymentMethod, String location) {
}
