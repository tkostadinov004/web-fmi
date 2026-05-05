create table feature_flags
(
    name    VARCHAR(255) PRIMARY KEY NOT NULL,
    "value" boolean                  NOT NULL
);