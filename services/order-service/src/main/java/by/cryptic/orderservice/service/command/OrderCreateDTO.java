package by.cryptic.orderservice.service.command;

import by.cryptic.utils.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateDTO(
        @NotNull(message = "Longitude should not be null")
        Double lon,
        @NotNull(message = "Latitude should not be null")
        Double lat,
        @NotNull(message = "Payment method should not be null")
        PaymentMethod paymentMethod,
        Integer limit) {
}
