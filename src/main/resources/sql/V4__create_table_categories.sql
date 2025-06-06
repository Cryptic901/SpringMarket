CREATE TABLE categories
(
    id          uuid PRIMARY KEY UNIQUE NOT NULL,
    name        VARCHAR                 NOT NULL UNIQUE,
    description TEXT
);