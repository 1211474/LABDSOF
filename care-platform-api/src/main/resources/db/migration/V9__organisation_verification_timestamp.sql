ALTER TABLE organisations
    ADD COLUMN verified_at TIMESTAMP WITH TIME ZONE;

UPDATE organisations
SET verified_at = CURRENT_TIMESTAMP
WHERE verified = TRUE;
