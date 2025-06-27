CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE role
(
    id         BIGINT       NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted    BOOLEAN      NOT NULL,
    name       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

ALTER TABLE role
    ADD CONSTRAINT uc_role_name UNIQUE (name);

ALTER TABLE company
    DROP COLUMN is_active;

ALTER TABLE users
    DROP COLUMN is_active;