ALTER TABLE cart DROP COLUMN user_id;
ALTER TABLE cart ADD COLUMN user_id uuid references users(id) UNIQUE;