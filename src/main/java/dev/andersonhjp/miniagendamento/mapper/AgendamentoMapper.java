package dev.andersonhjp.miniagendamento.mapper;

import dev.andersonhjp.miniagendamento.dto.AgendamentoCreateRequest;
import dev.andersonhjp.miniagendamento.dto.AgendamentoResponse;
import dev.andersonhjp.miniagendamento.dto.AgendamentoUpdateRequest;
import dev.andersonhjp.miniagendamento.model.Agendamento;
import dev.andersonhjp.miniagendamento.model.StatusAgendamento;

import java.time.LocalDateTime;

public class AgendamentoMapper {

    public static Agendamento toEntity(AgendamentoCreateRequest request) {
        return Agendamento.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .usuario(request.usuario())
                .status(StatusAgendamento.AGENDADO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public static AgendamentoResponse toResponse(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(),
                a.getTitulo(),
                a.getDescricao(),
                a.getDataInicio(),
                a.getDataFim(),
                a.getStatus(),
                a.getUsuario(),
                a.getCriadoEm(),
                a.getAtualizadoEm()
        );
    }

    public static void merge(Agendamento entity, AgendamentoUpdateRequest req) {
        if (req.titulo() != null) {
            entity.setTitulo(req.titulo());
        }
        if (req.descricao() != null) {
            entity.setDescricao(req.descricao());
        }
        if (req.dataInicio() != null) {
            entity.setDataInicio(req.dataInicio());
        }
        if (req.dataFim() != null) {
            entity.setDataFim(req.dataFim());
        }
    }
}
