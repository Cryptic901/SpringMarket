package by.cryptic.springmarket.exception;

public class NotEnoughProducts extends RuntimeException {
    public NotEnoughProducts(String message) {
        super(message);
    }
}
