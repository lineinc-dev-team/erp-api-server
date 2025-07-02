CREATE SEQUENCE IF NOT EXISTS client_company_contact_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS client_company_file_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS client_company_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS company_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;

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
    area_code       VARCHAR(255),
    phone_number    VARCHAR(255),
    email           VARCHAR(255),
    payment_method  VARCHAR(255) NOT NULL,
    payment_period  VARCHAR(255),
    is_active       BOOLEAN      NOT NULL,
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
    landline_number   VARCHAR(255),
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
    document_name      VARCHAR(255) NOT NULL,
    file_url           VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    memo               TEXT,
    CONSTRAINT pk_clientcompanyfile PRIMARY KEY (id)
);

CREATE TABLE company
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted    BOOLEAN      NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    name       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

CREATE TABLE role
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted    BOOLEAN      NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    name       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                BIGINT       NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE,
    updated_at        TIMESTAMP WITH TIME ZONE,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN      NOT NULL,
    deleted_at        TIMESTAMP WITH TIME ZONE,
    company_id        BIGINT       NOT NULL,
    login_id          VARCHAR(255) NOT NULL,
    username          VARCHAR(255),
    password_hash     VARCHAR(255),
    phone_number      VARCHAR(255),
    password_reset_at TIMESTAMP WITH TIME ZONE,
    last_login_at     TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    users_id BIGINT NOT NULL,
    roles_id BIGINT NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (users_id, roles_id)
);

ALTER TABLE company
    ADD CONSTRAINT uc_company_name UNIQUE (name);

ALTER TABLE role
    ADD CONSTRAINT uc_role_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_loginid UNIQUE (login_id);

CREATE INDEX idx_853cf762f970b23c4fd69a185 ON users (username);

ALTER TABLE client_company_contact
    ADD CONSTRAINT FK_CLIENTCOMPANYCONTACT_ON_CLIENT_COMPANY FOREIGN KEY (client_company_id) REFERENCES client_company (id);

ALTER TABLE client_company_file
    ADD CONSTRAINT FK_CLIENTCOMPANYFILE_ON_CLIENT_COMPANY FOREIGN KEY (client_company_id) REFERENCES client_company (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_id) REFERENCES role (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_users FOREIGN KEY (users_id) REFERENCES users (id);