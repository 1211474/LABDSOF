ALTER TABLE organisations
    ADD COLUMN official_email VARCHAR(320),
    ADD COLUMN official_phone VARCHAR(40),
    ADD COLUMN website VARCHAR(2048),
    ADD COLUMN coarse_location VARCHAR(120);
