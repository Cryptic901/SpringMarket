package by.cryptic.springmarket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum OrderStatus {

    CANCELLED("Order has cancelled"),
    IN_PROGRESS("Order has done"),
    COMPLETED("Order has been completed"),
    FAILED("Order has been failed"),
    IN_STOCK("Order in stock");

    private final String message;
}
