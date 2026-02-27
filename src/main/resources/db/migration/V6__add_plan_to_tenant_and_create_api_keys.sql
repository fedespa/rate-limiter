ALTER TABLE tenants
    ADD COLUMN plan_id UUID;

ALTER TABLE tenants
    ADD CONSTRAINT fk_tenants_plan
    FOREIGN KEY (plan_id)
    REFERENCES plans(id);

CREATE TABLE api_keys(
    id UUID PRIMARY KEY,
    key_hash VARCHAR(64) NOT NULL,
    tenant_id UUID NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_api_keys_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id)
        ON DELETE CASCADE
);