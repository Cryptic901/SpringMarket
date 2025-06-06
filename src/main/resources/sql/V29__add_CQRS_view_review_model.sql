CREATE TABLE review_view
(
    reviewId    uuid primary key not null unique,
    productId   uuid,
    title       varchar,
    rating      decimal(3, 1),
    description text,
    image       text,
    created_at  timestamptz,
    updated_at  timestamptz,
    created_by  uuid,
    updated_by  uuid
);