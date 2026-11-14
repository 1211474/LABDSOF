ALTER TABLE caregivers
    ADD COLUMN display_name VARCHAR(120),
    ADD COLUMN coarse_location VARCHAR(120);

UPDATE caregivers c
SET display_name = COALESCE(NULLIF(TRIM(u.email), ''), 'Cuidador'),
    coarse_location = 'Não definida'
FROM users u
WHERE c.user_id = u.id;

ALTER TABLE caregivers
    ALTER COLUMN display_name SET NOT NULL,
    ALTER COLUMN coarse_location SET NOT NULL;
