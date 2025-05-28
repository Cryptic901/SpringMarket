package by.cryptic.springmarket.event;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {

    private UUID orderId;
    private String userEmail;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;

}
