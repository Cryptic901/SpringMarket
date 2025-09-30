CREATE SCHEMA IF NOT EXISTS category_schema;
CREATE TABLE category_db.category_schema.categories
(
    id          uuid unique not null primary key,
    name        varchar(255),
    description text
);