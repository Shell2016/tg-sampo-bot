--liquibase formatted sql

--changeset michaelshell:1
ALTER TABLE users
ALTER COLUMN user_name DROP NOT NULL;

--changeset michaelshell:2
ALTER TABLE users_event
ALTER COLUMN partner_fullname TYPE VARCHAR(128);