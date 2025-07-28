ALTER TABLE user_schema.users drop column phone_number;
ALTER TABLE user_schema.users add column phone_number varchar(20);
ALTER TABLE user_schema.users drop cart_id;