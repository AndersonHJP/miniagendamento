package dev.andersonhjp.miniagendamento.controller;

import dev.andersonhjp.miniagendamento.dto.AgendamentoCreateRequest;
import dev.andersonhjp.miniagendamento.dto.AgendamentoResponse;
import dev.andersonhjp.miniagendamento.dto.AgendamentoUpdateRequest;
import dev.andersonhjp.miniagendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@AllArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoCreateRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponse>> listarAgenda() {
        return ResponseEntity.ok(service.listarAgendas());
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<AgendamentoResponse>> listarHoje() {
        return ResponseEntity.ok(service.buscarHoje());
    }

    @GetMapping("/semana")
    public ResponseEntity<List<AgendamentoResponse>> listarSemana(
            @RequestParam int ano,
            @RequestParam int semana) {

        return ResponseEntity.ok(service.buscarSemanaDoMes(ano, semana));
    }

    @GetMapping("/mes")
    public ResponseEntity<List<AgendamentoResponse>> listarMes(@RequestParam int ano,
                                                               @RequestParam int mes) {
        return ResponseEntity.ok(service.buscarMes(ano, mes));
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AgendamentoResponse atualizar(@PathVariable Long id,
                                         @Valid @RequestBody AgendamentoUpdateRequest request) {
        return service.atualizar(id, request);
    }

    @PutMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PutMapping("/{id}/concluir")
    public AgendamentoResponse concluir(@PathVariable Long id) {
        return service.concluir(id);
    }
}
