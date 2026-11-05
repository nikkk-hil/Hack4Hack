
-- =========================================
-- V7: SKILLS AND RECOMMENDATIONS
-- =========================================

-- =========================================
-- SKILLS MASTER TABLE
-- =========================================

CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(80) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_skill_name_not_blank
        CHECK (LENGTH(TRIM(name)) > 0)
);

CREATE UNIQUE INDEX uq_skills_name_lower
ON skills (LOWER(name));


-- =========================================
-- USER SKILLS
-- =========================================

CREATE TABLE user_skills (
    user_id UUID NOT NULL,

    skill_id UUID NOT NULL,

    -- Proficiency from 1 to 5
    proficiency SMALLINT NOT NULL DEFAULT 3,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_skills
        PRIMARY KEY (user_id, skill_id),

    CONSTRAINT fk_user_skills_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_skills_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id),

    CONSTRAINT chk_user_skill_proficiency
        CHECK (proficiency BETWEEN 1 AND 5)
);

CREATE INDEX idx_user_skills_skill
ON user_skills (skill_id);


-- =========================================
-- TEAM REQUIRED SKILLS
-- =========================================

CREATE TABLE team_required_skills (
    team_id UUID NOT NULL,

    skill_id UUID NOT NULL,

    -- Importance from 1 to 5
    importance SMALLINT NOT NULL DEFAULT 3,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_team_required_skills
        PRIMARY KEY (team_id, skill_id),

    CONSTRAINT fk_team_required_skills_team
        FOREIGN KEY (team_id)
        REFERENCES teams(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_team_required_skills_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id),

    CONSTRAINT chk_team_skill_importance
        CHECK (importance BETWEEN 1 AND 5)
);