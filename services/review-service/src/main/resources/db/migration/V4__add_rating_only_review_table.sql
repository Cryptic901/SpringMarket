CREATE TABLE review_schema.rating_only_review
(
    id         uuid not null unique primary key,
    product_id uuid not null,
    rating     numeric(2, 1),
    created_at timestamp,
    created_by uuid
)