
-- =========================================
-- V2: HACKATHON PARTICIPANTS
-- =========================================

CREATE TABLE hackathon_participants (
    hackathon_id UUID NOT NULL,

    user_id UUID NOT NULL,

    registered_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_hackathon_participants
        PRIMARY KEY (hackathon_id, user_id),

    CONSTRAINT fk_participant_hackathon
        FOREIGN KEY (hackathon_id)
        REFERENCES hackathons(id),

    CONSTRAINT fk_participant_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);