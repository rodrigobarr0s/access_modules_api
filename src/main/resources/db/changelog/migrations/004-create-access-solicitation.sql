--liquibase formatted sql

--changeset rodrigobarr0s:create-access-solicitation 
--comment: Criação da tabela de solicitações de acesso com todos os campos e constraints

CREATE TABLE access_solicitation (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    protocolo VARCHAR(50) NOT NULL UNIQUE,
    status INT NOT NULL, -- mapeado pelo enum SolicitationStatus
    justificativa VARCHAR(500),
    urgente BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    cancel_reason VARCHAR(200),
    negation_reason VARCHAR(500), 
    CONSTRAINT pk_access_solicitation PRIMARY KEY (id),
    CONSTRAINT fk_access_solicitation_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_access_solicitation_module FOREIGN KEY (module_id) REFERENCES modules(id)
);

--rollback DROP TABLE access_solicitation;
