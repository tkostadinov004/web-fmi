ALTER TABLE projects
    ADD COLUMN description VARCHAR(2000);
ALTER TABLE projects
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE projects
    ADD COLUMN created_by VARCHAR(100);

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_created_by
        FOREIGN KEY (created_by)
            REFERENCES users (username);