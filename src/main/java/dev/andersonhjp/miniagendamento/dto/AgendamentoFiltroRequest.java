package dev.andersonhjp.miniagendamento.dto;

import dev.andersonhjp.miniagendamento.model.StatusAgendamento;

import java.time.LocalDate;

public record AgendamentoFiltroRequest(
        StatusAgendamento status,
        LocalDate dataInicio,
        LocalDate dataFim,
        String usuario,
        String titulo
) {
}
