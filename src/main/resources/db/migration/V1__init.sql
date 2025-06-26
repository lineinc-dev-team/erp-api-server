CREATE SEQUENCE IF NOT EXISTS company_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE company
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted    BOOLEAN      NOT NULL,
    name       VARCHAR(255) NOT NULL,
    is_active  BOOLEAN      NOT NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                BIGINT       NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN      NOT NULL,
    company_id        BIGINT       NOT NULL,
    login_id          VARCHAR(255) NOT NULL,
    account_type      VARCHAR(255) NOT NULL,
    username          VARCHAR(255),
    password_hash     VARCHAR(255),
    phone_number      VARCHAR(255),
    is_active         BOOLEAN      NOT NULL,
    locked_at         TIMESTAMP WITHOUT TIME ZONE,
    password_reset_at TIMESTAMP WITHOUT TIME ZONE,
    last_login_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE company
    ADD CONSTRAINT uc_company_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_loginid UNIQUE (login_id);

CREATE INDEX idx_853cf762f970b23c4fd69a185 ON users (username);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);