-- liquibase formatted sql

-- changeSet yuvis:1
CREATE TABLE users (
    id SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    first_name VARCHAR(200),
    last_name VARCHAR(200),
    phone VARCHAR(30),
    email VARCHAR(200),
    password VARCHAR(200) NOT NULL,
    enabled boolean
);

-- changeSet yuvis:2
CREATE TABLE advertisements (
    id SERIAL NOT NULL PRIMARY KEY,
    title VARCHAR(200),
    description TEXT,
    price BIGINT
);

-- changeSet yuvis:3
CREATE TABLE comments (
    id SERIAL NOT NULL PRIMARY KEY,
    text TEXT,
    created_at TIMESTAMP
);

-- changeSet yuvis:4
CREATE TABLE pictures (
    id BIGINT NOT NULL PRIMARY KEY,
    file_path TEXT NULL,
    file_size BIGINT NULL,
    media_type VARCHAR(255),
    data TEXT
);

-- changeSet yuvis:5
CREATE TABLE authorities(
    id SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(50),
    authority VARCHAR(20)
);

