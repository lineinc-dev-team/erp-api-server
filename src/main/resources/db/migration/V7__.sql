CREATE SEQUENCE IF NOT EXISTS client_company_contact_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS client_company_file_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS client_company_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE client_company
(
    id              BIGINT       NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    deleted         BOOLEAN      NOT NULL,
    deleted_at      TIMESTAMP WITH TIME ZONE,
    name            VARCHAR(255) NOT NULL,
    business_number VARCHAR(255),
    ceo_name        VARCHAR(255),
    address         VARCHAR(255),
    phone_number    VARCHAR(255),
    email           VARCHAR(255),
    payment_method  VARCHAR(255) NOT NULL,
    payment_period  VARCHAR(255),
    active          BOOLEAN      NOT NULL,
    memo            TEXT,
    CONSTRAINT pk_clientcompany PRIMARY KEY (id)
);

CREATE TABLE client_company_contact
(
    id                BIGINT       NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE,
    updated_at        TIMESTAMP WITH TIME ZONE,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN      NOT NULL,
    deleted_at        TIMESTAMP WITH TIME ZONE,
    client_company_id BIGINT       NOT NULL,
    name              VARCHAR(255) NOT NULL,
    position          VARCHAR(255),
    phone_number      VARCHAR(255),
    email             VARCHAR(255),
    memo              TEXT,
    CONSTRAINT pk_clientcompanycontact PRIMARY KEY (id)
);

CREATE TABLE client_company_file
(
    id                 BIGINT       NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE,
    updated_at         TIMESTAMP WITH TIME ZONE,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    deleted            BOOLEAN      NOT NULL,
    deleted_at         TIMESTAMP WITH TIME ZONE,
    client_company_id  BIGINT       NOT NULL,
    file_url           VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    memo               TEXT,
    CONSTRAINT pk_clientcompanyfile PRIMARY KEY (id)
);

ALTER TABLE client_company_contact
    ADD CONSTRAINT FK_CLIENTCOMPANYCONTACT_ON_CLIENTCOMPANY FOREIGN KEY (client_company_id) REFERENCES client_company (id);

ALTER TABLE client_company_file
    ADD CONSTRAINT FK_CLIENTCOMPANYFILE_ON_CLIENTCOMPANY FOREIGN KEY (client_company_id) REFERENCES client_company (id);

