-- =========================
-- HOME SERVICE - V2
-- Allow MEMBER to be assigned to multiple homes
-- =========================

ALTER TABLE home_members
    DROP CONSTRAINT IF EXISTS home_members_member_user_id_key;

ALTER TABLE home_members
    ADD CONSTRAINT uq_home_members_home_user UNIQUE (home_id, member_user_id);

CREATE INDEX IF NOT EXISTS idx_home_members_member_user_id ON home_members(member_user_id);