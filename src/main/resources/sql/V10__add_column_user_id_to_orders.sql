ALTER TABLE orders
    ADD COLUMN user_id uuid;
ALTER TABLE orders
    DROP COLUMN id;
ALTER TABLE orders
    ADD COLUMN id uuid primary key UNIQUE NOT NULL;