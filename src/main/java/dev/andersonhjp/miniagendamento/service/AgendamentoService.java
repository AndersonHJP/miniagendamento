package dev.andersonhjp.miniagendamento.service;

import dev.andersonhjp.miniagendamento.dto.*;
import dev.andersonhjp.miniagendamento.mapper.AgendamentoMapper;
import dev.andersonhjp.miniagendamento.model.Agendamento;
import dev.andersonhjp.miniagendamento.model.StatusAgendamento;
import dev.andersonhjp.miniagendamento.repository.AgendamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;

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
    public Page<AgendamentoResponse> listarAgendamentos(Pageable pageable) {
        Page<Agendamento> paginaDeAgendamentos = repository.findAll(pageable);
        return paginaDeAgendamentos.map(AgendamentoMapper::toResponse);
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
        if (entity.getStatus() == StatusAgendamento.CANCELADO) {
            throw new IllegalArgumentException("Agendamento já está cancelado.");
        }
        entity.setStatus(StatusAgendamento.CANCELADO);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);

    }

    @Transactional
    public AgendamentoResponse definirStatusPendente(Long id) {
        Agendamento entity = buscarPorIdOuLancarExcecao(id);
        if (entity.getStatus() == StatusAgendamento.PENDENTE) {
            throw new IllegalArgumentException("Agendamento já está pendente.");
        }
        entity.setStatus(StatusAgendamento.PENDENTE);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse concluirAgendamento(Long id) {
        Agendamento entity = buscarPorIdOuLancarExcecao(id);
        if (entity.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new IllegalArgumentException("Agendamento já está concluido.");
        }
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

    @Transactional(readOnly = true)
    public Page<AgendamentoResponse> listar(AgendamentoFiltroRequest filtro, Pageable pageable) {
        Page<Agendamento> pageDeEntidades  = repository.buscarComFiltro(
                filtro.status(),
                filtro.dataInicio(),
                filtro.dataFim(),
                filtro.usuario(),
                filtro.titulo(),
                pageable
        );

        return pageDeEntidades.map(AgendamentoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AgendamentoResponse> buscarHoje() {
        IntervaloDatas intervalo = calcularIntervaloDia(LocalDate.now());
        return listar(new AgendamentoFiltroRequest(null, intervalo.inicio(), intervalo.fim(), null, null), Pageable.unpaged());
    }

    private IntervaloDatas calcularIntervaloDia(LocalDate dia) {
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(23, 59, 59, 999_999_999);
        return new IntervaloDatas(inicio, fim);
    }

    @Transactional(readOnly = true)
    public Page<AgendamentoResponse> buscarSemanaDoMes(int ano, int semana) {

        LocalDate dataBase = LocalDate.ofYearDay(ano, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, semana)
                .with(DayOfWeek.MONDAY);

        LocalDateTime inicio = dataBase.atStartOfDay();
        LocalDateTime fim = dataBase.with(DayOfWeek.SUNDAY).atTime(23, 59, 59, 999_999_999);

        return listar(new AgendamentoFiltroRequest(null, inicio, fim, null, null), Pageable.unpaged());
    }


    @Transactional(readOnly = true)
    public Page<AgendamentoResponse> buscarMes(int ano, int mes) {
        IntervaloDatas intervalo = calcularIntervaloMes(ano, mes);

        return listar(new AgendamentoFiltroRequest(null, intervalo.inicio(), intervalo.fim(), null, null), Pageable.unpaged());
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
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Datas inválidas: início e fim devem ser informados.");
        }
        if (!inicio.isBefore(fim)){
            throw new IllegalArgumentException("Intervalo inválido: a data de início precisa ser anterior à data de fim.");
        }
    }

    private void validarConflitoDeAgendamento(String usuario, LocalDateTime inicio, LocalDateTime fim, Long id) {
        if (repository.existsConflito(usuario, inicio, fim, id)) {
            throw new IllegalArgumentException("Conflito na agenda: Já existe um agendamento nesse periodo entre %s e %s."
                    .formatted(usuario, inicio, fim)
            );
        }
    }

}
