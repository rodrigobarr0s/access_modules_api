--liquibase formatted sql

--changeset rodrigobarr0s:create-users-table
--comment: create users table

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

--rollback DROP TABLE users;
