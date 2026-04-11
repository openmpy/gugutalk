CREATE INDEX idx_member_active ON member (id) WHERE deleted_at IS NULL;
CREATE INDEX idx_member_updated_at_id ON member (updated_at DESC, id DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_member_location ON member USING GIST (location);

CREATE INDEX idx_likes_liked_id ON likes (liked_id);