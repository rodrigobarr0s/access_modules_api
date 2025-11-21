--liquibase formatted sql

--changeset rodrigobarr0s:create-access-solicitation 
--comment: Criação da tabela de solicitações de acesso

CREATE TABLE access_solicitation (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_access_solicitation PRIMARY KEY (id),
    CONSTRAINT fk_access_solicitation_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_access_solicitation_module FOREIGN KEY (module_id) REFERENCES modules(id)
);

--rollback DROP TABLE access_solicitation;
