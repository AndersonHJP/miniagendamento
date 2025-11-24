package dev.andersonhjp.miniagendamento.controller;

import dev.andersonhjp.miniagendamento.dto.AgendamentoCreateRequest;
import dev.andersonhjp.miniagendamento.dto.AgendamentoFiltroRequest;
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
        List<AgendamentoResponse> lista = service.listarAgendas();
        return ResponseEntity.ok(lista);
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

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<AgendamentoResponse>> listar(AgendamentoFiltroRequest filtro) {
        return ResponseEntity.ok(service.listar(filtro));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<AgendamentoResponse>> listarHoje() {
        return ResponseEntity.ok(service.buscarHoje());
    }

//    @GetMapping("/semana")
//    public ResponseEntity<List<AgendamentoResponse>> listarSemana() {
//        return ResponseEntity.ok(service.buscarSemana());
//    }
//
//    @GetMapping("/mes")
//    public ResponseEntity<List<AgendamentoResponse>> listarMes() {
//        return ResponseEntity.ok(service.buscarMes());
//    }

}
