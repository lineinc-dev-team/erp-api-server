ALTER TABLE refresh_token
    DROP CONSTRAINT fk_refreshtoken_on_user;

DROP TABLE refresh_token CASCADE;

DROP SEQUENCE refresh_token_seq CASCADE;