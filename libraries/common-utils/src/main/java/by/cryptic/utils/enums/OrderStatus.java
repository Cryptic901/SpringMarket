package by.cryptic.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum OrderStatus {

    CANCELLED("Order has cancelled"),
    PENDING("Order in pending"),
    IN_PROGRESS_OF_DELIVERY("Order is in progress of delivery"),
    COMPLETED("Order has been completed"),
    IN_STOCK("Order in stock"),
    FAILED("Order failed. Please try again");

    private final String message;


}
