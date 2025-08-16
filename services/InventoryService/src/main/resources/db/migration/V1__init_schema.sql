CREATE SCHEMA IF NOT EXISTS inventory_schema;
CREATE TABLE inventory_schema.inventory
(
    id                 uuid primary key ,
    product_id         uuid,
    available_quantity int,
    reserved_quantity  int,
    warehouse_id       uuid,
    expires_at         timestamp
);