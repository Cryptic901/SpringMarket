CREATE TABLE inventory_schema.outbox (
    id uuid primary key,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id uuid not null,
    event_type VARCHAR(255) NOT NULL ,
    payload jsonb NOT NULL,
    created_at TIMESTAMP DEFAULT now()
)