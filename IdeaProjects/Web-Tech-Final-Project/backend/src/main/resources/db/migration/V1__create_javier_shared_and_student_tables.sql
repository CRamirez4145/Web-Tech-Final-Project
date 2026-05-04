CREATE TABLE sections (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sections_name UNIQUE (name)
);

CREATE TABLE teams (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    section_id BIGINT NOT NULL,
    instructor_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_teams_section FOREIGN KEY (section_id) REFERENCES sections (id)
);

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    team_id BIGINT,
    section_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_team FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_users_section FOREIGN KEY (section_id) REFERENCES sections (id)
);

ALTER TABLE teams
    ADD CONSTRAINT fk_teams_instructor FOREIGN KEY (instructor_id) REFERENCES users (id);

CREATE TABLE active_weeks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    week_number INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BIT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_active_weeks_week_number UNIQUE (week_number)
);

CREATE TABLE rubrics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE rubric_criteria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rubric_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_rubric_criteria_rubric FOREIGN KEY (rubric_id) REFERENCES rubrics (id)
);

CREATE TABLE weekly_activity_reports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    active_week_id BIGINT NOT NULL,
    submitted_at DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_war_student_week UNIQUE (student_id, active_week_id),
    CONSTRAINT fk_war_student FOREIGN KEY (student_id) REFERENCES users (id),
    CONSTRAINT fk_war_team FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_war_section FOREIGN KEY (section_id) REFERENCES sections (id),
    CONSTRAINT fk_war_active_week FOREIGN KEY (active_week_id) REFERENCES active_weeks (id)
);

CREATE TABLE war_activities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    war_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    hours_spent DECIMAL(5, 2) NOT NULL,
    category VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_war_activities_war FOREIGN KEY (war_id) REFERENCES weekly_activity_reports (id)
);

CREATE TABLE peer_evaluations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    evaluator_id BIGINT NOT NULL,
    evaluatee_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    active_week_id BIGINT NOT NULL,
    rubric_id BIGINT NOT NULL,
    submitted_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_peer_eval_unique UNIQUE (evaluator_id, evaluatee_id, active_week_id),
    CONSTRAINT fk_peer_eval_evaluator FOREIGN KEY (evaluator_id) REFERENCES users (id),
    CONSTRAINT fk_peer_eval_evaluatee FOREIGN KEY (evaluatee_id) REFERENCES users (id),
    CONSTRAINT fk_peer_eval_team FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_peer_eval_section FOREIGN KEY (section_id) REFERENCES sections (id),
    CONSTRAINT fk_peer_eval_active_week FOREIGN KEY (active_week_id) REFERENCES active_weeks (id),
    CONSTRAINT fk_peer_eval_rubric FOREIGN KEY (rubric_id) REFERENCES rubrics (id)
);

CREATE TABLE peer_evaluation_criterion_scores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    peer_evaluation_id BIGINT NOT NULL,
    criterion_id BIGINT NOT NULL,
    score INT NOT NULL,
    public_comment VARCHAR(500),
    private_comment VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT uk_peer_eval_score_unique UNIQUE (peer_evaluation_id, criterion_id),
    CONSTRAINT fk_peer_eval_score_evaluation FOREIGN KEY (peer_evaluation_id) REFERENCES peer_evaluations (id),
    CONSTRAINT fk_peer_eval_score_criterion FOREIGN KEY (criterion_id) REFERENCES rubric_criteria (id)
);
