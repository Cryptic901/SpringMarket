ALTER TABLE payment_schema.payments ADD COLUMN payment_status varchar(100);
ALTER TABLE payment_view_schema.payment_view ADD COLUMN payment_status varchar(100);