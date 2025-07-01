ALTER TABLE client_company_file
    ADD document_name VARCHAR(255);

ALTER TABLE client_company_file
    ALTER COLUMN document_name SET NOT NULL;