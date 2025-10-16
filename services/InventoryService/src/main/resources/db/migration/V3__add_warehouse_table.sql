CREATE TABLE IF NOT EXISTS inventory_schema.warehouse
(
    id           uuid primary key,
    name         VARCHAR(255) NOT NULL,
    location     GEOGRAPHY(POINT),
    type         VARCHAR(255) NOT NULL,
    is_active    bool         NOT NULL,
    capacity     BIGINT       NOT NULL,
    current_load BIGINT       NOT NULL,
    created_at   TIMESTAMP DEFAULT now(),
    updated_at   TIMESTAMP
);
CREATE INDEX idx_warehouse_location_gist ON inventory_schema.warehouse USING GIST (location)