package dev.andersonhjp.miniagendamento.controller;

import dev.andersonhjp.miniagendamento.dto.AgendamentoCreateRequest;
import dev.andersonhjp.miniagendamento.dto.AgendamentoResponse;
import dev.andersonhjp.miniagendamento.dto.AgendamentoUpdateRequest;
import dev.andersonhjp.miniagendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agendamentos")
@AllArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoCreateRequest request) {
        return service.criarAgendamento(request);
    }

    @GetMapping
    public ResponseEntity<Page<AgendamentoResponse>> listarAgenda(Pageable pageable) {
        Page<AgendamentoResponse> pagina = service.listarAgendamentos(pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/hoje")
    public ResponseEntity<Page<AgendamentoResponse>> listarHoje() {
        return ResponseEntity.ok(service.buscarHoje());
    }

    @GetMapping("/semana")
    public ResponseEntity<Page<AgendamentoResponse>> listarSemana(
            @RequestParam int ano,
            @RequestParam int semana) {

        return ResponseEntity.ok(service.buscarSemanaDoMes(ano, semana));
    }

    @GetMapping("/mes")
    public ResponseEntity<Page<AgendamentoResponse>> listarMes(@RequestParam int ano,
                                                               @RequestParam int mes) {
        return ResponseEntity.ok(service.buscarMes(ano, mes));
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarAgendamentoPorId(id);
    }

    @PutMapping("/{id}")
    public AgendamentoResponse atualizar(@PathVariable Long id,
                                         @Valid @RequestBody AgendamentoUpdateRequest request) {
        return service.atualizarAgendamento(id, request);
    }

    @PutMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return service.cancelarAgendamento(id);
    }

    @PutMapping("/{id}/pendente")
    public AgendamentoResponse definirPendente(@PathVariable Long id) {
        return service.definirStatusPendente(id);
    }

    @PutMapping("/{id}/concluir")
    public AgendamentoResponse concluir(@PathVariable Long id) {
        return service.concluirAgendamento(id);
    }
}
