CREATE TABLE user_view
(
    userId       uuid PRIMARY KEY UNIQUE NOT NULL,
    email        VARCHAR                 NOT NULL,
    username     VARCHAR(100)            NOT NULL,
    phone_number VARCHAR                 NOT NULL,
    gender       VARCHAR                 NOT NULL,
    createdAt    timestamptz
);