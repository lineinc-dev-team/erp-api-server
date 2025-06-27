CREATE SEQUENCE IF NOT EXISTS company_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

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

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_id) REFERENCES role (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_users FOREIGN KEY (users_id) REFERENCES users (id);