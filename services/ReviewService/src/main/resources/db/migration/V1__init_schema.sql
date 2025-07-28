CREATE SCHEMA IF NOT EXISTS review_schema;
CREATE TABLE review_schema.reviews
(
    id          uuid not null unique primary key,
    product_id  uuid not null,
    rating      numeric(2, 1),
    description text,
    image       text,
    title       varchar(255),
    created_at  timestamp,
    updated_at  timestamp,
    created_by  uuid,
    updated_by  uuid
);