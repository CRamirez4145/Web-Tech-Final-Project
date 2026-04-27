ALTER TABLE sections
    ADD COLUMN start_date DATE NULL,
    ADD COLUMN end_date DATE NULL,
    ADD COLUMN rubric_id BIGINT NULL,
    ADD CONSTRAINT fk_sections_rubric FOREIGN KEY (rubric_id) REFERENCES rubrics (id);
