
-- =========================================
-- AUDIT LOGS
-- =========================================

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    actor_user_id UUID REFERENCES users(id),

    -- Deliberately no FK to teams:
    -- historical logs survive physical team deletion.
    team_id UUID NOT NULL,

    hackathon_id UUID NOT NULL,

    action VARCHAR(80) NOT NULL,

    metadata JSONB NOT NULL DEFAULT '{}'::JSONB,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_audit_metadata_object
        CHECK (JSONB_TYPEOF(metadata) = 'object')
);

CREATE INDEX idx_audit_logs_team
ON audit_logs (team_id, created_at DESC);

CREATE INDEX idx_audit_logs_actor
ON audit_logs (actor_user_id, created_at DESC);


-- Prevent accidental updates and deletions.
CREATE OR REPLACE FUNCTION prevent_audit_log_mutation()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION
        'Audit logs are append-only. % is not allowed.',
        TG_OP;
END;
$$;

CREATE TRIGGER trg_audit_logs_immutable
BEFORE UPDATE OR DELETE ON audit_logs
FOR EACH ROW
EXECUTE FUNCTION prevent_audit_log_mutation();



-- =========================================
-- IDEMPOTENCY KEYS
-- =========================================

CREATE TABLE idempotency_keys (
    user_id UUID NOT NULL,

    idempotency_key VARCHAR(255) NOT NULL,

    operation VARCHAR(100) NOT NULL,

    -- SHA-256 hash of canonical request content.
    request_hash VARCHAR(64) NOT NULL,

    response_status SMALLINT,

    response_body JSONB,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    completed_at TIMESTAMP WITH TIME ZONE,

    -- Initial retention policy: 24 hours.
    expires_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT (
            CURRENT_TIMESTAMP + INTERVAL '24 hours'
        ),

    CONSTRAINT pk_idempotency_keys
        PRIMARY KEY (user_id, idempotency_key),

    CONSTRAINT fk_idempotency_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT chk_idempotency_key_not_blank
        CHECK (LENGTH(TRIM(idempotency_key)) > 0),

    CONSTRAINT chk_idempotency_operation_not_blank
        CHECK (LENGTH(TRIM(operation)) > 0),

    CONSTRAINT chk_idempotency_hash
        CHECK (request_hash ~ '^[0-9a-f]{64}$'),

    CONSTRAINT chk_idempotency_response_status
        CHECK (
            response_status IS NULL
            OR response_status BETWEEN 100 AND 599
        ),

    CONSTRAINT chk_idempotency_response_complete
        CHECK (
            (
                response_status IS NULL
                AND response_body IS NULL
                AND completed_at IS NULL
            )
            OR
            (
                response_status IS NOT NULL
                AND response_status BETWEEN 100 AND 599
                AND response_body IS NOT NULL
                AND completed_at IS NOT NULL
            )
        ),

    CONSTRAINT chk_idempotency_completion_time
        CHECK (
            completed_at IS NULL
            OR completed_at >= created_at
        ),

    CONSTRAINT chk_idempotency_expiry
        CHECK (expires_at > created_at)
);



-- =========================================
-- TRANSACTIONAL OUTBOX EVENTS
-- =========================================

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    event_type VARCHAR(100) NOT NULL,

    -- For example: TEAM, HACKATHON, JOIN_REQUEST
    aggregate_type VARCHAR(50) NOT NULL,

    aggregate_id UUID NOT NULL,

    hackathon_id UUID,

    event_payload JSONB NOT NULL,

    attempt_count INTEGER NOT NULL DEFAULT 0,

    next_attempt_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    locked_until TIMESTAMP WITH TIME ZONE,

    locked_by VARCHAR(100),

    last_error TEXT,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    published_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_outbox_attempt_count
        CHECK (attempt_count >= 0),

    CONSTRAINT chk_outbox_lock_pair
        CHECK (
            (locked_until IS NULL) = (locked_by IS NULL)
        ),

    CONSTRAINT chk_published_event_unlocked
        CHECK (
            published_at IS NULL
            OR (
                locked_until IS NULL
                AND locked_by IS NULL
            )
        ),

    CONSTRAINT chk_outbox_payload_object
        CHECK (JSONB_TYPEOF(event_payload) = 'object')
);



