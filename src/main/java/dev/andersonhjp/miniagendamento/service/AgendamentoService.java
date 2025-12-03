package dev.andersonhjp.miniagendamento.service;

import dev.andersonhjp.miniagendamento.dto.*;
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
import java.time.temporal.IsoFields;
import java.util.List;

@Service
@AllArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;

    @Transactional
    public AgendamentoResponse criarAgendamento(@Valid AgendamentoCreateRequest req) {

        validarIntervaloDeDatas(req.dataInicio(), req.dataFim());
        validarConflitoDeAgendamento(req.usuario(), req.dataInicio(), req.dataFim(), null);

        Agendamento entity = AgendamentoMapper.toEntity(req);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarAgendamentos() {
        List<Agendamento> agendamentos = repository.findAll();
        return agendamentos.stream()
                .map(AgendamentoMapper::toResponse)
                .toList();
    }

    @Transactional
    public AgendamentoResponse atualizarAgendamento(Long id, @Valid AgendamentoUpdateRequest request) {
        Agendamento entity = buscarPorIdOuLancarExcecao(id);
        AgendamentoMapper.merge(entity, request);
        validarIntervaloDeDatas(request.dataInicio(), request.dataFim());
        validarConflitoDeAgendamento(entity.getUsuario(), request.dataInicio(), request.dataFim(), entity.getId());

        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse cancelarAgendamento(Long id) {
        Agendamento entity = buscarPorIdOuLancarExcecao(id);
        entity.setStatus(StatusAgendamento.CANCELADO);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);

    }

    @Transactional
    public AgendamentoResponse concluirAgendamento(Long id) {
        Agendamento entity = buscarPorIdOuLancarExcecao(id);
        entity.setStatus(StatusAgendamento.CONCLUIDO);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);

    }

    @Transactional
    public AgendamentoResponse buscarAgendamentoPorId(Long id) {
        Agendamento entity = buscarPorIdOuLancarExcecao(id);
        return AgendamentoMapper.toResponse(entity);

    }
    // ------------------------------FILTROS-----------------------------------------------

    @Transactional
    public List<AgendamentoResponse> listar(AgendamentoFiltroRequest filtro) {
        List<Agendamento> lista = repository.buscarComFiltro(
                filtro.status(),
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
    public List<AgendamentoResponse> buscarHoje() {
        IntervaloDatas intervalo = calcularIntervaloDia(LocalDate.now());
        return listar(new AgendamentoFiltroRequest(null, intervalo.inicio(), intervalo.fim(), null, null));
    }

    private IntervaloDatas calcularIntervaloDia(LocalDate dia) {
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(23, 59, 59, 999_999_999);
        return new IntervaloDatas(inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> buscarSemanaDoMes(int ano, int semana) {

        LocalDate dataBase = LocalDate.ofYearDay(ano, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, semana)
                .with(DayOfWeek.MONDAY);

        LocalDateTime inicio = dataBase.atStartOfDay();
        LocalDateTime fim = dataBase.with(DayOfWeek.SUNDAY).atTime(23, 59, 59, 999_999_999);

        return listar(new AgendamentoFiltroRequest(null, inicio, fim, null, null));
    }


    @Transactional(readOnly = true)
    public List<AgendamentoResponse> buscarMes(int ano, int mes) {
        IntervaloDatas intervalo = calcularIntervaloMes(ano, mes);

        return listar(new AgendamentoFiltroRequest(null, intervalo.inicio(), intervalo.fim(), null, null));
    }

    private IntervaloDatas calcularIntervaloMes(int ano, int mes) {
        LocalDate dataBase = LocalDate.of(ano, mes, 1);

        LocalDateTime inicio = dataBase.atStartOfDay();
        LocalDateTime fim = dataBase.withDayOfMonth(dataBase.lengthOfMonth())
                .atTime(23, 59, 59, 999_999_999);

        return new IntervaloDatas(inicio, fim);
    }


//    ---------------------------------VALIDAÇÕES------------------------------------------

    private Agendamento buscarPorIdOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
    }

    private void validarIntervaloDeDatas(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("Intervalo invalido: dataInicio deve ser anterior a dataFim");
        }
    }

    private void validarConflitoDeAgendamento(String usuario, LocalDateTime inicio, LocalDateTime fim, Long id) {
        if (repository.existsConflito(usuario, inicio, fim, id)) {
            throw new IllegalArgumentException("Conflito na agenda: Já existe um agendamento nesse periodo");
        }
    }

}
