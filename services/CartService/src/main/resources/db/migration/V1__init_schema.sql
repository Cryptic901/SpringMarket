CREATE SCHEMA IF NOT EXISTS cart_schema;
CREATE TABLE IF NOT EXISTS cart_schema.carts
(
    id      uuid primary key unique not null,
    total   numeric(10, 2),
    user_id uuid                    not null
);
CREATE TABLE IF NOT EXISTS cart_schema.cart_products
(
    id         uuid primary key unique not null,
    quantity   int,
    product_id uuid                    not null,
    cart_id    uuid                    not null,
    price      numeric(10, 2)
);