package by.cryptic.exceptions.handler;

public class PaymentTooManyRequestException extends RuntimeException {
    public PaymentTooManyRequestException(String message) {
        super(message);
    }
}
