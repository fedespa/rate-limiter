CREATE TABLE IF NOT EXISTS invitations (

    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    role_to_assign VARCHAR(50) NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_invitations_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_invitation_pending
ON invitations (email, tenant_id)
WHERE status = 'PENDING';