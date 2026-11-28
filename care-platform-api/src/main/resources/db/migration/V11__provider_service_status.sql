ALTER TABLE users
    ADD COLUMN organisation_id UUID REFERENCES organisations (id);

ALTER TABLE support_services
    ADD COLUMN official_notice VARCHAR(500);

UPDATE users
SET organisation_id = '11111111-1111-1111-1111-111111111111'
WHERE email = 'sofia@example.com';
