CREATE TABLE order_view
(
    orderId        uuid primary key not null unique,
    payment_method varchar          not null,
    order_status   varchar(255),
    location       text,
    created_at     timestamptz,
    updated_at     timestamptz,
    created_by     uuid,
    updated_by     uuid
);