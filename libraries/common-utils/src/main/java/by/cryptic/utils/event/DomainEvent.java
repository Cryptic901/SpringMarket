package by.cryptic.utils.event;

import by.cryptic.utils.event.cart.CartAddedItemEvent;
import by.cryptic.utils.event.cart.CartClearedEvent;
import by.cryptic.utils.event.cart.CartDeletedProductEvent;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import by.cryptic.utils.event.category.CategoryDeletedEvent;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import by.cryptic.utils.event.order.*;
import by.cryptic.utils.event.payment.PaymentCanceledEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import by.cryptic.utils.event.review.ReviewCreatedEvent;
import by.cryptic.utils.event.review.ReviewDeletedEvent;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import by.cryptic.utils.event.user.UserCreatedEvent;
import by.cryptic.utils.event.user.UserDeletedEvent;
import by.cryptic.utils.event.user.UserLoginedEvent;
import by.cryptic.utils.event.user.UserUpdatedEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "UserCreatedEvent"),
        @JsonSubTypes.Type(value = UserDeletedEvent.class, name = "UserDeletedEvent"),
        @JsonSubTypes.Type(value = UserLoginedEvent.class, name = "UserLoginedEvent"),
        @JsonSubTypes.Type(value = UserUpdatedEvent.class, name = "UserUpdatedEvent"),

        @JsonSubTypes.Type(value = ReviewCreatedEvent.class, name = "ReviewCreatedEvent"),
        @JsonSubTypes.Type(value = ReviewDeletedEvent.class, name = "ReviewDeletedEvent"),
        @JsonSubTypes.Type(value = ReviewUpdatedEvent.class, name = "ReviewUpdatedEvent"),

        @JsonSubTypes.Type(value = ProductCreatedEvent.class, name = "ProductCreatedEvent"),
        @JsonSubTypes.Type(value = ProductDeletedEvent.class, name = "ProductDeletedEvent"),
        @JsonSubTypes.Type(value = ProductUpdatedEvent.class, name = "ProductUpdatedEvent"),

        @JsonSubTypes.Type(value = PaymentCanceledEvent.class, name = "PaymentCanceledEvent"),
        @JsonSubTypes.Type(value = PaymentCreatedEvent.class, name = "PaymentCreatedEvent"),
        @JsonSubTypes.Type(value = PaymentFailedEvent.class, name = "PaymentFailedEvent"),
        @JsonSubTypes.Type(value = PaymentSuccessEvent.class, name = "PaymentSuccessEvent"),

        @JsonSubTypes.Type(value = OrderCanceledEvent.class, name = "OrderCanceledEvent"),
        @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "OrderCreatedEvent"),
        @JsonSubTypes.Type(value = OrderFailedEvent.class, name = "OrderFailedEvent"),
        @JsonSubTypes.Type(value = OrderSuccessEvent.class, name = "OrderSuccessEvent"),
        @JsonSubTypes.Type(value = OrderUpdatedEvent.class, name = "OrderUpdatedEvent"),

        @JsonSubTypes.Type(value = CategoryCreatedEvent.class, name = "CategoryCreatedEvent"),
        @JsonSubTypes.Type(value = CategoryDeletedEvent.class, name = "CategoryDeletedEvent"),
        @JsonSubTypes.Type(value = CategoryUpdatedEvent.class, name = "CategoryUpdatedEvent"),

        @JsonSubTypes.Type(value = CartAddedItemEvent.class, name = "CartAddedItemEvent"),
        @JsonSubTypes.Type(value = CartClearedEvent.class, name = "CartClearedEvent"),
        @JsonSubTypes.Type(value = CartDeletedProductEvent.class, name = "CartDeletedProductEvent"),
})
@Data
public abstract class DomainEvent {
    private UUID eventId;
    private LocalDateTime timestamp;

    public DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
    }

}
