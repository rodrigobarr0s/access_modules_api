--liquibase formatted sql

--changeset rodrigobarr0s:create-module-incompatibility 
--comment: Criação da tabela de incompatibilidades entre módulos

CREATE TABLE module_incompatibility (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    module_id BIGINT NOT NULL,
    incompatible_module_id BIGINT NOT NULL,
    CONSTRAINT pk_module_incompatibility PRIMARY KEY (id),
    CONSTRAINT fk_module_incompatibility_module FOREIGN KEY (module_id) REFERENCES modules(id),
    CONSTRAINT fk_module_incompatibility_incompatible FOREIGN KEY (incompatible_module_id) REFERENCES modules(id)
);

--rollback DROP TABLE module_incompatibility;
