ALTER TABLE invitations
  ADD COLUMN IF NOT EXISTS revoked BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS revoked_at TIMESTAMP NULL;

ALTER TABLE invitations
  DROP CONSTRAINT IF EXISTS chk_invitations_revoked_at;

ALTER TABLE invitations
  ADD CONSTRAINT chk_invitations_revoked_at
  CHECK (
    (revoked = FALSE AND revoked_at IS NULL)
    OR
    (revoked = TRUE AND revoked_at IS NOT NULL)
  );

ALTER TABLE invitations
  DROP CONSTRAINT IF EXISTS chk_invitations_not_consumed_and_revoked;

ALTER TABLE invitations
  ADD CONSTRAINT chk_invitations_not_consumed_and_revoked
  CHECK (NOT (consumed = TRUE AND revoked = TRUE));

CREATE INDEX IF NOT EXISTS idx_invitations_active
ON invitations (home_id, email, expires_at)
WHERE consumed = FALSE AND revoked = FALSE;