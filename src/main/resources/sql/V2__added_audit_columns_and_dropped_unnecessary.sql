DROP TABLE IF EXISTS cart;
ALTER TABLE orders
    DROP COLUMN IF EXISTS user_id;
ALTER TABLE orders
    ADD COLUMN created_by VARCHAR;
ALTER TABLE orders
    ADD COLUMN id uuid;
ALTER TABLE products
    ADD COLUMN created_by VARCHAR;
ALTER TABLE reviews
    DROP COLUMN IF EXISTS user_id;
ALTER TABLE reviews
    ADD COLUMN title VARCHAR(255);
ALTER TABLE reviews
    RENAME COLUMN createdat TO created_at;
ALTER TABLE reviews
    ADD COLUMN updated_at timestamptz;
ALTER TABLE reviews
    ADD COLUMN created_by VARCHAR(255);
ALTER TABLE reviews
    ADD COLUMN updated_by VARCHAR(255);
ALTER TABLE users
    ADD COLUMN created_at timestamptz;
