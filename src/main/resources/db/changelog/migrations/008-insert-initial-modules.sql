--liquibase formatted sql

--changeset rodrigobarr0s:insert-module-portal
INSERT INTO modules (name, description) VALUES ('Portal do Colaborador', 'Todos os departamentos');
--rollback DELETE FROM modules WHERE name = 'Portal do Colaborador';

--changeset rodrigobarr0s:insert-module-relatorios
INSERT INTO modules (name, description) VALUES ('Relatórios Gerenciais', 'Todos os departamentos');
--rollback DELETE FROM modules WHERE name = 'Relatórios Gerenciais';

--changeset rodrigobarr0s:insert-module-financeira
INSERT INTO modules (name, description) VALUES ('Gestão Financeira', 'Financeiro, TI');
--rollback DELETE FROM modules WHERE name = 'Gestão Financeira';

--changeset rodrigobarr0s:insert-module-aprovador
INSERT INTO modules (name, description) VALUES ('Aprovador Financeiro', 'Financeiro, TI - incompatível com Solicitante Financeiro');
--rollback DELETE FROM modules WHERE name = 'Aprovador Financeiro';

--changeset rodrigobarr0s:insert-module-solicitante
INSERT INTO modules (name, description) VALUES ('Solicitante Financeiro', 'Financeiro, TI - incompatível com Aprovador Financeiro');
--rollback DELETE FROM modules WHERE name = 'Solicitante Financeiro';

--changeset rodrigobarr0s:insert-module-admin-rh
INSERT INTO modules (name, description) VALUES ('Administrador RH', 'RH, TI - incompatível com Colaborador RH');
--rollback DELETE FROM modules WHERE name = 'Administrador RH';

--changeset rodrigobarr0s:insert-module-colab-rh
INSERT INTO modules (name, description) VALUES ('Colaborador RH', 'RH, TI - incompatível com Administrador RH');
--rollback DELETE FROM modules WHERE name = 'Colaborador RH';

--changeset rodrigobarr0s:insert-module-estoque
INSERT INTO modules (name, description) VALUES ('Gestão de Estoque', 'Operações, TI');
--rollback DELETE FROM modules WHERE name = 'Gestão de Estoque';

--changeset rodrigobarr0s:insert-module-compras
INSERT INTO modules (name, description) VALUES ('Compras', 'Operações, TI');
--rollback DELETE FROM modules WHERE name = 'Compras';

--changeset rodrigobarr0s:insert-module-auditoria
INSERT INTO modules (name, description) VALUES ('Auditoria', 'Apenas TI');
--rollback DELETE FROM modules WHERE name = 'Auditoria';
