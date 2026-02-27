-- =========================
-- HOME SERVICE - V1
-- =========================

-- =========================
-- HOMES
-- =========================
CREATE TABLE homes (
    id BIGSERIAL PRIMARY KEY,
    home_id INTEGER NOT NULL UNIQUE,

    status VARCHAR(32) NOT NULL,

    address VARCHAR(255),
    street_number VARCHAR(32),
    city VARCHAR(128),

    price_per_kwh DOUBLE PRECISION,

    head_user_id UUID,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_homes_price_positive CHECK (price_per_kwh IS NULL OR price_per_kwh > 0)
);

CREATE INDEX idx_homes_status ON homes(status);
CREATE INDEX idx_homes_head_user_id ON homes(head_user_id);

-- =========================
-- INVITATIONS
-- =========================
CREATE TABLE invitations (
    id UUID PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,

    home_id INTEGER NOT NULL,
    email VARCHAR(255) NOT NULL,

    expires_at TIMESTAMP NOT NULL,
    consumed BOOLEAN NOT NULL DEFAULT FALSE,
    consumed_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_invitations_consumed_at
        CHECK (
            (consumed = FALSE AND consumed_at IS NULL)
            OR
            (consumed = TRUE AND consumed_at IS NOT NULL)
        )
);

CREATE INDEX idx_invitations_home_email ON invitations(home_id, email);
CREATE INDEX idx_invitations_expires_consumed ON invitations(expires_at, consumed);

-- =========================
-- HOME MEMBERS
-- =========================
CREATE TABLE home_members (
    id BIGSERIAL PRIMARY KEY,
    home_id INTEGER NOT NULL,
    member_user_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_home_members_home_id ON home_members(home_id);

-- =========================
-- ANALYST HOMES
-- =========================
CREATE TABLE analyst_homes (
    id BIGSERIAL PRIMARY KEY,
    analyst_user_id UUID NOT NULL,
    home_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uq_analyst_home UNIQUE (analyst_user_id, home_id)
);

CREATE INDEX idx_analyst_homes_analyst_user_id ON analyst_homes(analyst_user_id);
CREATE INDEX idx_analyst_homes_home_id ON analyst_homes(home_id);