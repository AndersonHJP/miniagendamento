package dev.andersonhjp.miniagendamento.service;

import dev.andersonhjp.miniagendamento.dto.AgendamentoCreateRequest;
import dev.andersonhjp.miniagendamento.dto.AgendamentoFiltroRequest;
import dev.andersonhjp.miniagendamento.dto.AgendamentoResponse;
import dev.andersonhjp.miniagendamento.dto.AgendamentoUpdateRequest;
import dev.andersonhjp.miniagendamento.mapper.AgendamentoMapper;
import dev.andersonhjp.miniagendamento.model.Agendamento;
import dev.andersonhjp.miniagendamento.model.StatusAgendamento;
import dev.andersonhjp.miniagendamento.repository.AgendamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;

    @Transactional
    public AgendamentoResponse criar(@Valid AgendamentoCreateRequest req) {

        validarIntervalor(req.dataInicio(), req.dataFim());
        checarConflito(req.usuario(), req.dataInicio(), req.dataFim(), null);

        Agendamento entity = AgendamentoMapper.toEntity(req);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarAgendas() {
        List<Agendamento> agendamentos = repository.findAll();
        return agendamentos.stream()
                .map(AgendamentoMapper::toResponse)
                .toList();
    }

    @Transactional
    public AgendamentoResponse atualizar(Long id, @Valid AgendamentoUpdateRequest request) {
        Agendamento entity = buscarOurFalhar(id);
        AgendamentoMapper.merge(entity, request);
        validarIntervalor(request.dataInicio(), request.dataFim());
        checarConflito(entity.getUsuario(), request.dataInicio(), request.dataFim(), entity.getId());

        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id) {
        Agendamento entity = buscarOurFalhar(id);
        entity.setStatus(StatusAgendamento.CANCELADO);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);

    }

    @Transactional
    public AgendamentoResponse concluir(Long id) {
        Agendamento entity = buscarOurFalhar(id);
        entity.setStatus(StatusAgendamento.CONCLUIDO);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);

    }

    @Transactional
    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento entity = buscarOurFalhar(id);
        return AgendamentoMapper.toResponse(entity);

    }
    // ------------------------------FILTROS-----------------------------------------------

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listar(AgendamentoFiltroRequest filtro) {
        List<Agendamento> lista = repository.buscarComFiltro(filtro.status(),
                filtro.dataInicio(),
                filtro.dataFim(),
                filtro.usuario(),
                filtro.titulo()
        );
        return lista.stream()
                .map(AgendamentoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> buscarHoje(){
        LocalDateTime hoje = LocalDateTime.now();
        return listar(new AgendamentoFiltroRequest(null, hoje, hoje,null,null));
    }

//    @Transactional(readOnly = true)
//    public List<AgendamentoResponse> buscarSemana(){
//        LocalDate hoje = LocalDate.now();
//        LocalDate inicio = hoje.with(DayOfWeek.MONDAY);
//        LocalDate fim = hoje.with(DayOfWeek.SUNDAY);
//        return listar(new AgendamentoFiltroRequest(null, inicio, fim, null, null));
//    }
//
//    @Transactional(readOnly = true)
//    public List<AgendamentoResponse> buscarMes() {
//        LocalDate hoje = LocalDate.now();
//        LocalDate inicio = hoje.withDayOfMonth(1);
//        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
//        return listar(new AgendamentoFiltroRequest(null, inicio, fim, null, null));
//    }



    private Agendamento buscarOurFalhar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
    }

    private void validarIntervalor(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("Intervalo invalido: dataInicio deve ser anterior a dataFim");
        }
    }

    private void checarConflito(String usuario, LocalDateTime inicio, LocalDateTime fim, Long id) {
        if (repository.existsConflito(usuario, inicio, fim, id)) {
            throw new IllegalArgumentException("Conflito na agenda: Já existe um agendamento nesse periodo");
        }
    }

}
