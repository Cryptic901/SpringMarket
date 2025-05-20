ALTER TABLE users ADD column enabled BOOLEAN;
ALTER TABLE users ADD column role VARCHAR(100);
ALTER TABLE products ADD column category VARCHAR(255);
ALTER TABLE orders ADD column order_status VARCHAR(255);

