CREATE TABLE cart_view
(
    cartId   uuid primary key not null unique,
    total    numeric(10, 2),
    products jsonb
);