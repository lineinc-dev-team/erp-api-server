CREATE SEQUENCE IF NOT EXISTS company_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE company
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    name       VARCHAR(255) NOT NULL,
    is_active  BOOLEAN      NOT NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

ALTER TABLE company
    ADD CONSTRAINT uc_company_name UNIQUE (name);
