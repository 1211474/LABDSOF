CREATE TABLE emergency_contacts (
    id UUID PRIMARY KEY,
    caregiver_id UUID NOT NULL REFERENCES caregivers (id),
    display_name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(40) NOT NULL,
    relationship VARCHAR(60)
);
