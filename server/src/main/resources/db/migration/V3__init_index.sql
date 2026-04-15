CREATE INDEX idx_member_active_updated
    ON member (updated_at DESC, id DESC)
    WHERE deleted_at IS NULL;