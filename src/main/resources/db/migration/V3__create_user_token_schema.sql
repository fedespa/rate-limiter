CREATE TABLE IF NOT EXISTS user_tokens (

    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_user_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_user_tokens_active_hash
    ON user_tokens(token_hash)
    WHERE revoked = FALSE AND used_at IS NULL;

CREATE INDEX idx_user_tokens_user ON user_tokens(user_id);