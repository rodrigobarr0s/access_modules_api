--liquibase formatted sql

--changeset rodrigobarr0s:create-modules-table
--comment: Criação da tabela de módulos

CREATE TABLE modules (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    CONSTRAINT pk_modules PRIMARY KEY (id)
);

--rollback DROP TABLE modules;
