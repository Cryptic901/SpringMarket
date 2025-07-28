CREATE SCHEMA IF NOT EXISTS user_schema;
CREATE TABLE IF NOT EXISTS user_schema.users (
    id uuid unique primary key not null,
    username varchar(100),
    password text,
    phone_number varchar(20),
    gender char,
    verify_code int,
    email varchar,
    created_at timestamp,
    enabled boolean,
    role varchar(100),
    verification_expires timestamp
);