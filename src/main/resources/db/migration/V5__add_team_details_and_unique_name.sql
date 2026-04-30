ALTER TABLE teams
    ADD COLUMN description VARCHAR(255) NULL,
    ADD COLUMN website VARCHAR(255) NULL;

ALTER TABLE teams
    ADD CONSTRAINT uk_teams_name UNIQUE (name);
