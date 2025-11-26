--liquibase formatted sql

--changeset rodrigobarr0s:create-modules-table
--comment: Criação da tabela de módulos e tabela auxiliar de departamentos

CREATE TABLE modules (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_modules PRIMARY KEY (id)
);

-- Tabela de departamentos permitidos para cada módulo
CREATE TABLE module_departments (
    module_id BIGINT NOT NULL,
    department VARCHAR(255) NOT NULL,
    CONSTRAINT fk_module_departments_module FOREIGN KEY (module_id) REFERENCES modules(id)
);

--rollback DROP TABLE module_departments;
--rollback DROP TABLE modules;
