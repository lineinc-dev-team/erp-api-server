ALTER TABLE client_company
    ADD COLUMN is_active BOOLEAN DEFAULT true;

UPDATE client_company
SET is_active = true
WHERE is_active IS NULL;

ALTER TABLE client_company
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE client_company
    DROP COLUMN active;