create table audit_logs
(
    uuid          varchar(255) primary key,
    message       varchar      not null,
    type          varchar,
    time          timestamp,
    user_username varchar(100) not null references users (username)
);