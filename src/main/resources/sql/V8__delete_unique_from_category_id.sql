ALTER TABLE products
    DROP COLUMN category_id;
ALTER TABLE products
    ADD COLUMN category_id uuid;