package by.cryptic.exceptions;

public class PaymentTooManyRequestException extends RuntimeException {
    public PaymentTooManyRequestException(String message) {
        super(message);
    }
}
