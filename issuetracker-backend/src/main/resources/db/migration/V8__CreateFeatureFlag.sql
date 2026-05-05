create table feature_flags
(
    name    VARCHAR(255) PRIMARY KEY NOT NULL,
    "value" VARCHAR(1024)
);