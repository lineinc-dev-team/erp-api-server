ALTER TABLE client_company
    ADD landline_number VARCHAR(255);

ALTER TABLE client_company
    DROP COLUMN area_code;

ALTER TABLE client_company
    DROP COLUMN phone_number;