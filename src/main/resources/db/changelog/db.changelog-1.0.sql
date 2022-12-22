--liquibase formatted sql

--changeset michaelshell:1
CREATE TABLE IF NOT EXISTS users
(
    id BIGINT PRIMARY KEY ,
    user_name VARCHAR(64) NOT NULL UNIQUE ,
    first_name VARCHAR(64) NOT NULL ,
    last_name VARCHAR(64),
    role VARCHAR(32),
    status VARCHAR(32),
    registered_at TIMESTAMP NOT NULL
);


--changeset michaelshell:2
CREATE TABLE IF NOT EXISTS event
(
    id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(64) NOT NULL,
    info VARCHAR(256) DEFAULT '',
    time TIMESTAMP,
    created_at TIMESTAMP NOT NULL ,
    created_by VARCHAR(64) NOT NULL ,
    UNIQUE (name, time)
);

--changeset michaelshell:3
CREATE TABLE IF NOT EXISTS users_event
(
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE ,
    event_id BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE ,
    signed_at TIMESTAMP NOT NULL ,
    UNIQUE (event_id, user_id)
);

--changeset michaelshell:4
CREATE TABLE IF NOT EXISTS couple
(
    id BIGSERIAL PRIMARY KEY ,
    leader_name VARCHAR(64) NOT NULL ,
    follower_name VARCHAR(64) NOT NULL ,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE,
    signed_at TIMESTAMP NOT NULL
);

--changeset michaelshell:5
-- CREATE TABLE IF NOT EXISTS couple_event
-- (
--     id BIGSERIAL PRIMARY KEY ,
--     couple_id BIGINT NOT NULL REFERENCES couple (id) ON DELETE CASCADE ,
--     event_id BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE ,
--     signed_at TIMESTAMP,
--     UNIQUE (couple_id, event_id)
-- );