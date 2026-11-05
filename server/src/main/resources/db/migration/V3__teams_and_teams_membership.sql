
-- =========================================
-- V3: TEAMS AND TEAM MEMBERSHIPS
-- =========================================

CREATE TYPE team_status AS ENUM (
    'OPEN',
    'FINALIZED',
    'CANCELLED'
);

-- =========================================
-- TEAMS
-- =========================================

CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    hackathon_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,

    description TEXT,

    leader_user_id UUID NOT NULL,

    status team_status NOT NULL DEFAULT 'OPEN',

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    finalized_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_teams_id_hackathon
        UNIQUE (id, hackathon_id),

    CONSTRAINT fk_teams_hackathon
        FOREIGN KEY (hackathon_id)
        REFERENCES hackathons(id),

    CONSTRAINT chk_team_name_not_blank
        CHECK (LENGTH(TRIM(name)) > 0),

    CONSTRAINT chk_finalized_timestamp
        CHECK (
            status <> 'FINALIZED'
            OR finalized_at IS NOT NULL
        ),

    CONSTRAINT chk_open_team_timestamp
        CHECK (
            status <> 'OPEN'
            OR finalized_at IS NULL
        )
);


-- =========================================
-- TEAM MEMBERSHIPS
-- =========================================

CREATE TABLE team_memberships (
    team_id UUID NOT NULL,

    hackathon_id UUID NOT NULL,

    user_id UUID NOT NULL,

    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_team_memberships
        PRIMARY KEY (team_id, user_id),

    -- One team per participant per hackathon
    CONSTRAINT uq_membership_hackathon_user
        UNIQUE (hackathon_id, user_id),

    CONSTRAINT fk_membership_team
        FOREIGN KEY (team_id, hackathon_id)
        REFERENCES teams(id, hackathon_id)
        ON DELETE CASCADE,

    -- Participant must be registered for this hackathon
    CONSTRAINT fk_membership_participant
        FOREIGN KEY (hackathon_id, user_id)
        REFERENCES hackathon_participants(hackathon_id, user_id)
);


-- =========================================
-- LEADER MUST BE A TEAM MEMBER
-- =========================================

ALTER TABLE teams
ADD CONSTRAINT fk_team_leader_membership
FOREIGN KEY (id, leader_user_id)
REFERENCES team_memberships(team_id, user_id)
DEFERRABLE INITIALLY DEFERRED;

