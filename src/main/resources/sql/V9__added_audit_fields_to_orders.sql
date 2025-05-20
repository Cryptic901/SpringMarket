ALTER TABLE orders ADD COLUMN created_at timestamptz;
ALTER TABLE orders ADD COLUMN updated_at timestamptz;
ALTER TABLE orders ADD COLUMN updated_by uuid;