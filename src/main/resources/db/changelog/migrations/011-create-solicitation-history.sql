--liquibase formatted sql

--changeset rodrigobarr0s:create-solicitation-history
--comment: Criação da tabela de histórico de solicitações

CREATE TABLE solicitation_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    solicitation_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL, -- enum HistoryAction
    reason VARCHAR(500),
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_solicitation_history PRIMARY KEY (id),
    CONSTRAINT fk_solicitation_history_solicitation FOREIGN KEY (solicitation_id) REFERENCES access_solicitation(id)
);

--rollback DROP TABLE solicitation_history;
