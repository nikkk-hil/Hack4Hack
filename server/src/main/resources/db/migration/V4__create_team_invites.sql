
-- =========================================
-- V4: TEAM INVITATIONS
-- =========================================

CREATE TABLE team_invites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code_hash VARCHAR(64) NOT NULL,

    team_id UUID NOT NULL,

    hackathon_id UUID NOT NULL,

    created_by UUID NOT NULL,

    expires_at TIMESTAMPTZ NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    revoked_at TIMESTAMPTZ,

    CONSTRAINT uq_team_invite_code_hash
        UNIQUE (code_hash),

    CONSTRAINT fk_invite_team
        FOREIGN KEY (team_id, hackathon_id)
        REFERENCES teams(id, hackathon_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_invite_creator
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT chk_invite_hash_format
        CHECK (code_hash ~ '^[0-9a-f]{64}$'),

    CONSTRAINT chk_invite_expiry
        CHECK (expires_at > created_at),

    CONSTRAINT chk_invite_revocation
        CHECK (
            revoked_at IS NULL
            OR revoked_at >= created_at
        )
);