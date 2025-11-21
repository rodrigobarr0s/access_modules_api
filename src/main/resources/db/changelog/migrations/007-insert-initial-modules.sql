--liquibase formatted sql

--changeset rodrigobarr0s:insert-initial-modules 
--comment: Inserção dos módulos iniciais

INSERT INTO modules (name, description) VALUES
('FINANCEIRO', 'Acesso ao módulo financeiro'),
('RH', 'Acesso ao módulo de recursos humanos'),
('OPERACOES', 'Acesso ao módulo de operações'),
('TI', 'Acesso ao módulo de tecnologia da informação');

--rollback DELETE FROM modules WHERE name IN ('FINANCEIRO','RH','OPERACOES','TI');
