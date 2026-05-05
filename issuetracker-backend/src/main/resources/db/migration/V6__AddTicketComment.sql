CREATE TABLE ticket_comments
(
    uuid            VARCHAR(255) PRIMARY KEY,
    content         VARCHAR(1024) NOT NULL,
    created_at      TIMESTAMP     NOT NULL,
    ticket_code     VARCHAR(100)  NOT NULL,
    author_username VARCHAR(100)  NOT NULL,

    CONSTRAINT fk_comment_ticket
        FOREIGN KEY (ticket_code)
            REFERENCES tickets (code)
            ON DELETE CASCADE,

    CONSTRAINT fk_comment_user
        FOREIGN KEY (author_username)
            REFERENCES users (username)
            ON DELETE CASCADE
);