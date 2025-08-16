package by.cryptic.utils.event;

public enum EventType {
    CartAddedItemEvent, CartClearedEvent, CartDeletedProductEvent,

    CategoryCreatedEvent, CategoryUpdatedEvent, CategoryDeletedEvent,

    StockCreatedEvent, StockReservationFailedEvent, StockReservedEvent,

    OrderCanceledEvent, OrderSuccessEvent, OrderFailedEvent, OrderUpdatedEvent, OrderCreatedEvent,
    FinalizeOrderEvent,

    ProductCreatedEvent, ProductUpdatedEvent, ProductDeletedEvent, ProductUpdatedQuantityFromStockEvent,

    ReviewCreatedEvent, ReviewUpdatedEvent, ReviewDeletedEvent,

    UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent, UserLoginedEvent, UserLogoutEvent,

    PaymentSuccessEvent, PaymentFailedEvent, PaymentCreatedEvent, PaymentCanceledEvent
}
