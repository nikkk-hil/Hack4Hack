-- =========================================
-- V1: Create Users and Hackathons
-- =========================================

CREATE TYPE hackathon_status AS ENUM (
    'DRAFT',
    'REGISTRATION_OPEN',
    'REGISTRATION_CLOSED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED'
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(254) NOT NULL,
    password_hash TEXT NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    time_zone VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,

    CONSTRAINT chk_user_email_not_empty CHECK (LENGTH(TRIM(email)) > 0),
    CONSTRAINT chk_user_name_not_empty CHECK (LENGTH(TRIM(display_name)) > 0)
);

CREATE UNIQUE INDEX unique_user_email_lower ON users (LOWER(email)) WHERE deleted_at IS NULL;

CREATE TABLE hackathons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    min_team_size SMALLINT NOT NULL,
    max_team_size SMALLINT NOT NULL,
    registration_opens_at TIMESTAMPTZ NOT NULL,
    registration_closes_at TIMESTAMPTZ NOT NULL,
    status hackathon_status NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_name_is_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT chk_min_team_size CHECK (min_team_size > 0),
    CONSTRAINT chk_max_team_size CHECK (max_team_size >= min_team_size),
    CONSTRAINT chk_registration_dates CHECK (registration_closes_at > registration_opens_at)
);