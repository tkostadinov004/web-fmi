CREATE TABLE sprints
(
    uuid VARCHAR(40) PRIMARY KEY
);

CREATE TABLE tickets
(
    code              VARCHAR(100) NOT NULL,
    project_uuid      VARCHAR(255) NOT NULL,
    title             VARCHAR(100) NOT NULL,
    description       VARCHAR(500),

    ticket_status     VARCHAR(50),
    ticket_priority   VARCHAR(50)  NOT NULL,

    sprint_uuid       VARCHAR(255),
    create_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date          TIMESTAMP,

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
            ON DELETE SET NULL,

    PRIMARY KEY (code, project_uuid)
);

CREATE TABLE ticket_dependencies
(
    ticket_code                    VARCHAR(100) NOT NULL,
    ticket_project_uuid            VARCHAR(255) NOT NULL,
    depends_on_ticket_code         VARCHAR(100) NOT NULL,
    depends_on_ticket_project_uuid VARCHAR(255) NOT NULL,

    PRIMARY KEY (ticket_code, ticket_project_uuid, depends_on_ticket_code, depends_on_ticket_project_uuid),

    CONSTRAINT fk_ticket_dep_main
        FOREIGN KEY (ticket_code, ticket_project_uuid)
            REFERENCES tickets (code, project_uuid)
            ON DELETE CASCADE,

    CONSTRAINT fk_ticket_dep_depends
        FOREIGN KEY (depends_on_ticket_code, depends_on_ticket_project_uuid)
            REFERENCES tickets (code, project_uuid)
            ON DELETE CASCADE
);