CREATE TABLE author (
  id BIGSERIAL  PRIMARY KEY,
  first_name    VARCHAR(100) NOT NULL,
  last_name     VARCHAR(100) NOT NULL,
  middle_name   VARCHAR(100) DEFAULT NULL
);

CREATE TABLE manga (
  id            BIGSERIAL PRIMARY KEY,
  title         VARCHAR(255) NOT NULL,
  release_date  DATE NOT NULL,
  author_id     BIGINT,
  constraint author_id foreign key (author_id) references author (id)
);