ALTER TABLE user_schema.users ADD COLUMN IF NOT EXISTS cart_id uuid;
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS password;
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS email;
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS role;
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS enabled;
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS verify_code;
ALTER TABLE user_schema.users DROP COLUMN IF EXISTS verification_expires;