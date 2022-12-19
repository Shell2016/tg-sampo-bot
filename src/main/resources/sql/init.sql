--liquibase formatted sql

--changeset michaelshell:1
CREATE TABLE IF NOT EXISTS users
(
    id BIGINT PRIMARY KEY ,
    user_name VARCHAR(64) NOT NULL UNIQUE ,
    first_name VARCHAR(64),
    name VARCHAR(64),
    role VARCHAR(32),
    admin BOOLEAN DEFAULT FALSE,
    registered_at TIMESTAMP
);


--changeset michaelshell:2
CREATE TABLE IF NOT EXISTS event
(
    id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    time TIMESTAMP
);

--changeset michaelshell:3
CREATE TABLE IF NOT EXISTS users_event
(
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE ,
    event_id BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE ,
    signed_at TIMESTAMP,
    UNIQUE (user_id, event_id)
);

--changeset michaelshell:4
CREATE TABLE IF NOT EXISTS couple
(
    id BIGSERIAL PRIMARY KEY ,
    couple_name VARCHAR(64) NOT NULL ,
    created_by BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

--changeset michaelshell:5
CREATE TABLE IF NOT EXISTS couple_event
(
    id BIGSERIAL PRIMARY KEY ,
    couple_id BIGINT NOT NULL REFERENCES couple (id) ON DELETE CASCADE ,
    event_id BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE ,
    signed_at TIMESTAMP,
    UNIQUE (couple_id, event_id)
);