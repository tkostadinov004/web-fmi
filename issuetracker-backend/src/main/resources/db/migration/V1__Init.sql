create table users (
    username VARCHAR(100) PRIMARY KEY,
    first_name VARCHAR(200) NOT NULL,
    last_name VARCHAR(200) NOT NULL,
    password VARCHAR NOT NULL
);

create table tokens (
    uuid VARCHAR(40) PRIMARY KEY,
    token_value VARCHAR(1024) NOT NULL,
    user_username VARCHAR(40) REFERENCES users(username)
);

create table projects (
    uuid VARCHAR(40) PRIMARY KEY,
    name VARCHAR(500) NOT NULL
);

create table project_users (
    project_uuid VARCHAR(40) REFERENCES projects(uuid),
    user_username VARCHAR(40) REFERENCES users(username),
    role VARCHAR,

    PRIMARY KEY (project_uuid, user_username, role)
);