CREATE TABLE manga (
  id BIGSERIAL NOT NULL,
  title VARCHAR(255) NOT NULL,
  release_date timestamp,
  author_id BIGINT
);