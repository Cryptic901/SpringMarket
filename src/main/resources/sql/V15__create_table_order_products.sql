CREATE TABLE order_products (
    id uuid PRIMARY KEY UNIQUE not null,
    order_id uuid references orders(id),
    product_id uuid references products(id)
);

ALTER TABLE users ADD COLUMN order_id uuid references orders(id);
ALTER TABLE users ADD COLUMN review_id uuid references reviews(id);
ALTER TABLE cart ADD COLUMN cart_product_id uuid references cart_products(id);
ALTER TABLE categories ADD COLUMN product_id uuid references products(id);
