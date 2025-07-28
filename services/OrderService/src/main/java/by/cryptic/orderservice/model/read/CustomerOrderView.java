package by.cryptic.orderservice.model.read;

import by.cryptic.utils.OrderStatus;
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
@Document(collection = "order_view")
public class CustomerOrderView {

    @MongoId
    private UUID orderId;

    private OrderStatus orderStatus;

    private UUID paymentId;

    private String location;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UUID createdBy;

    private UUID updatedBy;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CustomerOrderView that = (CustomerOrderView) o;
        return Objects.equals(orderId, that.orderId) &&
                orderStatus == that.orderStatus &&
                Objects.equals(paymentId, that.paymentId) &&
                Objects.equals(location, that.location) &&
                Objects.equals(price, that.price) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderStatus, paymentId, location, price, createdBy, updatedBy);
    }
}
