package dev.andersonhjp.miniagendamento.dto;

import dev.andersonhjp.miniagendamento.model.StatusAgendamento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AgendamentoFiltroRequest(
        StatusAgendamento status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String usuario,
        String titulo
) {
}
