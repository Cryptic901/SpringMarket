package by.cryptic.exceptions;

public class InsufficientWarehouseCapacityException extends RuntimeException {
    public InsufficientWarehouseCapacityException(String message) {
        super(message);
    }
}
