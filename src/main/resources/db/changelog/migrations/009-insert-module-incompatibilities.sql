--liquibase formatted sql

--changeset rodrigobarr0s:insert-incompat-aprovador-solicitante
INSERT INTO module_incompatibility (module_id, incompatible_module_id)
VALUES (
  (SELECT id FROM modules WHERE name='Aprovador Financeiro'),
  (SELECT id FROM modules WHERE name='Solicitante Financeiro')
);
--rollback DELETE FROM module_incompatibility;

--changeset rodrigobarr0s:insert-incompat-solicitante-aprovador
INSERT INTO module_incompatibility (module_id, incompatible_module_id)
VALUES (
  (SELECT id FROM modules WHERE name='Solicitante Financeiro'),
  (SELECT id FROM modules WHERE name='Aprovador Financeiro')
);
--rollback DELETE FROM module_incompatibility;

--changeset rodrigobarr0s:insert-incompat-admin-colab
INSERT INTO module_incompatibility (module_id, incompatible_module_id)
VALUES (
  (SELECT id FROM modules WHERE name='Administrador RH'),
  (SELECT id FROM modules WHERE name='Colaborador RH')
);
--rollback DELETE FROM module_incompatibility;

--changeset rodrigobarr0s:insert-incompat-colab-admin
INSERT INTO module_incompatibility (module_id, incompatible_module_id)
VALUES (
  (SELECT id FROM modules WHERE name='Colaborador RH'),
  (SELECT id FROM modules WHERE name='Administrador RH')
);
--rollback DELETE FROM module_incompatibility;
