package by.cryptic.paymentservice.model.read;

import by.cryptic.utils.PaymentMethod;
import by.cryptic.utils.PaymentStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payment_view")
public class PaymentView {

    @MongoId
    private UUID paymentId;

    private PaymentMethod paymentMethod;

    private UUID userId;

    private UUID orderId;

    private BigDecimal price;

    private LocalDateTime timestamp;

    private PaymentStatus paymentStatus;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentView that = (PaymentView) o;
        return Objects.equals(paymentId, that.paymentId) &&
                paymentMethod == that.paymentMethod &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(price, that.price) &&
                paymentStatus == that.paymentStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, paymentMethod, userId, orderId, price, paymentStatus);
    }
}
