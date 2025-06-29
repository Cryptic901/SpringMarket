package by.cryptic.cartservice.exception;

public class NotEnoughProducts extends RuntimeException {
    public NotEnoughProducts(String message) {
        super(message);
    }
}
