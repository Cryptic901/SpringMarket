CREATE SCHEMA IF NOT EXISTS product_schema;
CREATE TABLE IF NOT EXISTS product_schema.categories
(
    id          uuid unique not null primary key,
    name        varchar(255),
    description text
);
CREATE TABLE IF NOT EXISTS product_schema.products
(
    id          uuid unique not null primary key,
    name        varchar(255),
    quantity    int,
    description text,
    image       text,
    category_id uuid references product_schema.categories (id),
    price       numeric(10, 2) default 0,
    created_by  uuid,
    updated_by  uuid,
    created_at  timestamp,
    updated_at  timestamp
);
