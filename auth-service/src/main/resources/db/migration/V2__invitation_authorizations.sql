-- V2__invitation_authorizations.sql
-- Auth Service

CREATE TABLE invitation_authorizations (
    token_hash VARCHAR(64) PRIMARY KEY,

    email VARCHAR(255) NOT NULL,
    home_id INTEGER NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    status VARCHAR(16) NOT NULL,

    CONSTRAINT chk_inv_auth_status
        CHECK (status IN ('VALID','REVOKED','CONSUMED'))
);

CREATE INDEX idx_inv_auth_home_email ON invitation_authorizations(home_id, email);

CREATE INDEX idx_inv_auth_expires_at ON invitation_authorizations(expires_at);

CREATE INDEX idx_inv_auth_status ON invitation_authorizations(status);