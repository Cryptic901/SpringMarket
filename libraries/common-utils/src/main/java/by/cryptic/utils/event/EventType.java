package by.cryptic.utils.event;

public enum EventType {
    CartAddedItemEvent, CartClearedEvent, CartDeletedProductEvent,

    CategoryCreatedEvent, CategoryUpdatedEvent, CategoryDeletedEvent,

    OrderCanceledEvent, OrderSuccessEvent, OrderFailedEvent, OrderUpdatedEvent, OrderCreatedEvent,

    ProductCreatedEvent, ProductUpdatedEvent, ProductDeletedEvent,

    ReviewCreatedEvent, ReviewUpdatedEvent, ReviewDeletedEvent,

    UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent, UserLoginedEvent,

    PaymentSuccessEvent, PaymentFailedEvent, PaymentCreatedEvent, PaymentCanceledEvent
}
