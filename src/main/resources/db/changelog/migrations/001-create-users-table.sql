--liquibase formatted sql

--changeset rodrigobarr0s:create-users-table
--comment: Criação da tabela de usuários

CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role INTEGER NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

--rollback DROP TABLE users;
