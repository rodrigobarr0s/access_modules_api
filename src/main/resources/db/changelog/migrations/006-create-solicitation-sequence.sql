--liquibase formatted sql

--changeset rodrigobarr0s:create-solicitation-sequence
--comment: Criação da sequence para geração de protocolos de AccessSolicitation

CREATE SEQUENCE solicitation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--rollback DROP SEQUENCE solicitation_seq;
