
-- =========================================
-- V5: JOIN REQUESTS
-- =========================================

CREATE TYPE join_request_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'DECLINED',
    'WITHDRAWN',
    'CANCELLED'
);

CREATE TABLE join_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- References a currently existing team.
    -- Becomes NULL if that team is deleted.
    team_id UUID,

    -- Retains the original team identity.
    team_id_snapshot UUID NOT NULL,

    hackathon_id UUID NOT NULL,

    requester_id UUID NOT NULL,

    -- Nullable when request came from the public board.
    invite_id UUID,

    status join_request_status
        NOT NULL DEFAULT 'PENDING',

    message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    decided_at TIMESTAMPTZ,

    decided_by UUID,

    CONSTRAINT fk_request_team
        FOREIGN KEY (team_id, hackathon_id)
        REFERENCES teams(id, hackathon_id),

    CONSTRAINT fk_request_participant
        FOREIGN KEY (hackathon_id, requester_id)
        REFERENCES hackathon_participants(hackathon_id, user_id),

    CONSTRAINT fk_request_invite
        FOREIGN KEY (invite_id)
        REFERENCES team_invites(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_request_decided_by
        FOREIGN KEY (decided_by)
        REFERENCES users(id),

    -- Snapshot must match the current team when it exists.
    CONSTRAINT chk_request_team_snapshot
        CHECK (
            team_id IS NULL
            OR team_id = team_id_snapshot
        ),

    -- A pending request must reference an existing team.
    CONSTRAINT chk_pending_request_team
        CHECK (
            status <> 'PENDING'
            OR team_id IS NOT NULL
        ),

    -- Validate the decision metadata.
    CONSTRAINT chk_request_decision
        CHECK (
            (
                status = 'PENDING'
                AND decided_at IS NULL
                AND decided_by IS NULL
            )
            OR
            (
                status IN (
                    'ACCEPTED',
                    'DECLINED',
                    'WITHDRAWN'
                )
                AND decided_at IS NOT NULL
                AND decided_by IS NOT NULL
            )
            OR
            (
                status = 'CANCELLED'
                AND decided_at IS NOT NULL
            )
        ),

    CONSTRAINT chk_request_decision_time
        CHECK (
            decided_at IS NULL
            OR decided_at >= created_at
        )
);


-- Only one pending request per participant per team.
-- Historical requests can remain in the table.

CREATE UNIQUE INDEX uq_pending_join_request
ON join_requests (team_id, requester_id)
WHERE status = 'PENDING';