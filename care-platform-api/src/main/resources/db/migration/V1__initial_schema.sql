CREATE TABLE organisations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE support_services (
    id UUID PRIMARY KEY,
    organisation_id UUID NOT NULL REFERENCES organisations (id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    coarse_location VARCHAR(120) NOT NULL,
    operating_hours VARCHAR(120) NOT NULL,
    current_status VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(40) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_support_services_type ON support_services (type);
CREATE INDEX idx_support_services_location ON support_services (coarse_location);
