CREATE SCHEMA IF NOT EXISTS order_schema;
CREATE TABLE order_schema.orders (
    id uuid primary key unique not null,
    payment_method varchar(255),
    location text,
    created_at timestamp,
    updated_at timestamp,
    created_by uuid,
    updated_by uuid,
    user_id uuid,
    order_status varchar(255),
    price numeric(10,2)
);
CREATE TABLE order_schema.order_products (
    id uuid primary key unique not null,
    order_id uuid,
    product_id uuid,
    quantity int default 0
);