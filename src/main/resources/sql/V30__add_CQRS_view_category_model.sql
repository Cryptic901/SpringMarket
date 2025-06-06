CREATE TABLE category_view
(
    categoryId  uuid primary key unique not null,
    name        varchar(150),
    description text,
    products    jsonb
);