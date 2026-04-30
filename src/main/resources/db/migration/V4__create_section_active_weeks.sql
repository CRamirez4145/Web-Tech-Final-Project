CREATE TABLE section_active_weeks (
    section_id BIGINT NOT NULL,
    active_week_id BIGINT NOT NULL,
    PRIMARY KEY (section_id, active_week_id),
    CONSTRAINT fk_section_active_weeks_section FOREIGN KEY (section_id) REFERENCES sections (id),
    CONSTRAINT fk_section_active_weeks_active_week FOREIGN KEY (active_week_id) REFERENCES active_weeks (id)
);
