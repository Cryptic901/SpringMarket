package by.cryptic.utils.event.payment;

import java.util.UUID;

@FunctionalInterface
public interface PaymentEvent {
    UUID getPaymentId();
}
