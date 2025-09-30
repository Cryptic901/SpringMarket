package by.cryptic.orderservice.service.command;

import by.cryptic.utils.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateDTO(
        @NotBlank(message = "Location should not be null")
        String location,
        @NotNull(message = "Payment method should not be null")
        PaymentMethod paymentMethod) {
}
