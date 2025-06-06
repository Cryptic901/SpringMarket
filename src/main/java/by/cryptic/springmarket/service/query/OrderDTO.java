package by.cryptic.springmarket.service.query;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;

import java.util.List;
import java.util.UUID;

public record OrderDTO(PaymentMethod paymentMethod,
                       List<UUID> productIDs,
                       String location,
                       OrderStatus orderStatus) {
}
