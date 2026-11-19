ALTER TABLE care_recipients
    ADD COLUMN display_name VARCHAR(120);

UPDATE care_recipients
SET display_name = 'Pessoa acompanhada'
WHERE display_name IS NULL;

ALTER TABLE care_recipients
    ALTER COLUMN display_name SET NOT NULL;
