ALTER TABLE cart_products
    ADD COLUMN product_id uuid references products (id);
ALTER TABLE cart_products
    DROP cart_id;
ALTER TABLE cart_products
    ADD COLUMN cart_id uuid references cart (id);
ALTER TABLE cart_products
    ADD COLUMN price NUMERIC(10, 2) DEFAULT 0.00;
