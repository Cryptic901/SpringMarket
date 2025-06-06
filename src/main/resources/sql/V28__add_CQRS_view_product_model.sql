CREATE TABLE product_view
(
    productId   uuid primary key unique not null,
    name        varchar,
    price       numeric(10, 2),
    quantity    int,
    description text,
    image       text,
    created_by  uuid
);