CREATE SCHEMA IF NOT EXISTS auth_schema;
CREATE TABLE IF NOT EXISTS auth_schema.auth_user (
    id uuid primary key unique not null,
    username varchar(100),
    password varchar(100),
    email text,
    role varchar(100),
    enabled boolean,
    verify_code int,
    verification_expires timestamp
);