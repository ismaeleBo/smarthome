-- V1__init.sql
-- Auth Service

-- UUID generator
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    birth_date DATE,

    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),

    role   VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_role   ON users(role);
CREATE INDEX idx_users_status ON users(status);

ALTER TABLE users
    ADD CONSTRAINT chk_users_role
        CHECK (role IN ('ADMIN','HEAD','MEMBER','ANALYST'));

ALTER TABLE users
    ADD CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE','INACTIVE','DISABLED'));

-- =========================
-- ACTIVATION TOKENS
-- =========================
CREATE TABLE activation_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    token VARCHAR(128) NOT NULL UNIQUE,

    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    consumed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_activation_tokens_user_id   ON activation_tokens(user_id);
CREATE INDEX idx_activation_tokens_expires   ON activation_tokens(expires_at);
CREATE INDEX idx_activation_tokens_consumed  ON activation_tokens(consumed);