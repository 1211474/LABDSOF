CREATE TABLE caregivers (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users (id),
    phone_number VARCHAR(40) NOT NULL
);

CREATE TABLE care_recipients (
    id UUID PRIMARY KEY,
    caregiver_id UUID NOT NULL REFERENCES caregivers (id)
);

CREATE TABLE care_recipient_accessibility_tags (
    recipient_id UUID NOT NULL REFERENCES care_recipients (id),
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (recipient_id, tag)
);
