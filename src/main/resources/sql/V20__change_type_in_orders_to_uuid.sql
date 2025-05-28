ALTER TABLE orders
    DROP created_by;
ALTER TABLE orders
    ADD COLUMN created_by uuid;
ALTER TABLE products
    DROP created_by;
ALTER TABLE products
    ADD COLUMN created_by uuid;
ALTER TABLE reviews
    DROP created_by;
ALTER TABLE reviews
    ADD COLUMN created_by uuid;
ALTER TABLE reviews
    DROP updated_by;
ALTER TABLE reviews
    ADD COLUMN updated_by uuid;