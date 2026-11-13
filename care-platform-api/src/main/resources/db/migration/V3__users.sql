CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Demo accounts for the walking skeleton. Replace these credentials before deployment.
INSERT INTO users (id, email, password_hash, role, enabled)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'ana@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CAREGIVER', TRUE),
    ('00000000-0000-0000-0000-000000000002', 'sofia@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROVIDER', TRUE);
