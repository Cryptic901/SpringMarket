CREATE TABLE IF NOT EXISTS products
(
    id          uuid PRIMARY KEY UNIQUE NOT NULL,
    name        VARCHAR(255)            NOT NULL,
    price       decimal(5, 2) default 0.0,
    quantity    int,
    description text,
    image       TEXT
);

CREATE TABLE IF NOT EXISTS reviews
(
    id          uuid PRIMARY KEY UNIQUE       NOT NULL,
    user_id     uuid REFERENCES users (id)    NOT NULL,
    product_id  uuid REFERENCES products (id) NOT NULL,
    rating      decimal(3, 1),
    description TEXT,
    image       text,
    createdAt   timestamptz                   NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id           uuid PRIMARY KEY UNIQUE NOT NULL,
    username     VARCHAR(100)            NOT NULL,
    password     text                    NOT NULL,
    phone_number VARCHAR                 NOT NULL,
    gender       VARCHAR                 NOT NULL,
    is_verified  boolean,
    verify_code  INTEGER
);

CREATE TABLE IF NOT EXISTS cart
(
    user_id    uuid REFERENCES users (id),
    product_id uuid REFERENCES products (id)
);

CREATE TABLE IF NOT EXISTS orders
(
    user_id        uuid REFERENCES users (id),
    payment_method VARCHAR not null,
    product_id     uuid REFERENCES products (id),
    location       text    not null
);
