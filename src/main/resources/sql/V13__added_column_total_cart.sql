ALTER TABLE cart
    ADD column total numeric(10, 2) default 0;
ALTER TABLE products
    DROP COLUMN price;
ALTER TABLE products
    ADD COLUMN price numeric(10, 2) default 0;