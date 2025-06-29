package by.cryptic.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum OrderStatus {

    CANCELLED("Order has cancelled"),
    PENDING("Order in pending"),
    IN_PROGRESS("Order has done"),
    COMPLETED("Order has been completed"),
    IN_STOCK("Order in stock"),
    FAILED("Order failed. Please try again");

    private final String message;
}
