ALTER TABLE tb_agendamento DROP CONSTRAINT IF EXISTS ck_status;
ALTER TABLE tb_agendamento DROP CONSTRAINT IF EXISTS tb_agendamento_status_check;

ALTER TABLE tb_agendamento
    ADD CONSTRAINT ck_status CHECK (status IN ('AGENDADO', 'CANCELADO', 'PENDENTE', 'CONCLUIDO'));