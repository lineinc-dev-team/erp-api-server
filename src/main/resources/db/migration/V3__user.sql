CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE "user"
(
    id                BIGINT       NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
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
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_loginid UNIQUE (login_id);

CREATE INDEX idx_ea3c6abe1998db9734bcd155c ON "user" (username);

ALTER TABLE "user"
    ADD CONSTRAINT FK_USER_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);