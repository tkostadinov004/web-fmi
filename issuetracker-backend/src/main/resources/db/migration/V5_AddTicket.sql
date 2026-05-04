CREATE TABLE tickets
(
    uuid              VARCHAR(255) PRIMARY KEY,
    title             VARCHAR(100) NOT NULL UNIQUE,
    description       VARCHAR(500),

    ticket_status     VARCHAR(50),
    ticket_priority   VARCHAR(50)  NOT NULL,

    sprint_uuid       VARCHAR(255),
    create_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date          TIMESTAMP,

    project_uuid      VARCHAR(255) NOT NULL,
    assignee_username VARCHAR(100),

    CONSTRAINT fk_ticket_sprint
        FOREIGN KEY (sprint_uuid)
            REFERENCES sprints (uuid)
            ON DELETE SET NULL,

    CONSTRAINT fk_ticket_project
        FOREIGN KEY (project_uuid)
            REFERENCES projects (uuid)
            ON DELETE CASCADE,

    CONSTRAINT fk_ticket_user
        FOREIGN KEY (assignee_username)
            REFERENCES users (username)
            ON DELETE SET NULL
);

CREATE TABLE ticket_dependencies
(
    ticket_uuid            VARCHAR(255) NOT NULL,
    depends_on_ticket_uuid VARCHAR(255) NOT NULL,

    PRIMARY KEY (ticket_uuid, depends_on_ticket_uuid),

    CONSTRAINT fk_ticket_dep_main
        FOREIGN KEY (ticket_uuid)
            REFERENCES tickets (uuid)
            ON DELETE CASCADE,

    CONSTRAINT fk_ticket_dep_depends
        FOREIGN KEY (depends_on_ticket_uuid)
            REFERENCES tickets (uuid)
            ON DELETE CASCADE
);