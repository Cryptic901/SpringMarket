CREATE SCHEMA IF NOT EXISTS payment_schema;
CREATE SCHEMA IF NOT EXISTS payment_view_schema;

CREATE TABLE IF NOT EXISTS payment_schema.payments
(
    id             uuid unique not null primary key,
    payment_method varchar(100),
    user_id        uuid        not null,
    order_id       uuid        not null,
    price          numeric(10, 2),
    timestamp      timestamp
);

CREATE TABLE IF NOT EXISTS payment_view_schema.payment_view
(
    payment_id     uuid unique not null primary key,
    payment_method varchar(100),
    user_id        uuid        not null,
    order_id       uuid        not null,
    price          numeric(10, 2),
    created_at     timestamp
);
