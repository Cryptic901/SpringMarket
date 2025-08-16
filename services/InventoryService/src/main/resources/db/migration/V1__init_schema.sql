CREATE SCHEMA IF NOT EXISTS inventory_schema;
CREATE TABLE inventory_schema.inventory
(
    id                 uuid primary key,
    product_id         uuid,
    warehouse_id       uuid,
    available_quantity int
);

CREATE TABLE inventory_schema.reservaton
(
    id                  uuid primary key,
    product_id          uuid,
    quantity_to_reserve int,
    order_id            uuid,
    warehouse_id        uuid
);