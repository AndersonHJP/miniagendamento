package dev.andersonhjp.miniagendamento.repository;

import dev.andersonhjp.miniagendamento.model.Agendamento;
import dev.andersonhjp.miniagendamento.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
                FROM Agendamento a
                WHERE a.usuario = :usuario
                    AND a.status = dev.andersonhjp.miniagendamento.model.StatusAgendamento.AGENDADO
                    AND (a.dataInicio < :fim AND a.dataFim > :inicio)
                    AND (:ignoreId is NULL OR a.id <> :ignoreId)
            """)
    boolean existsConflito(@Param("usuario") String usuario,
                           @Param("inicio")LocalDateTime inicio,
                           @Param("fim")LocalDateTime fim,
                           @Param("ignoreId")Long ignoreId);


    // ---- 1. BUSCA COM FILTRO (para endpoints de listagem) ----
    @Query("""
        SELECT a FROM Agendamento a
        WHERE (:status IS NULL OR a.status = :status)
          AND (:dataInicio IS NULL OR a.dataInicio >= :dataInicio)
          AND (:dataFim IS NULL OR a.dataFim <= :dataFim)
          AND (:usuario IS NULL OR a.usuario = :usuario)
          AND (:titulo IS NULL OR LOWER(a.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
    """)
    List<Agendamento> buscarComFiltro(
            @Param("status") StatusAgendamento status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("usuario") String usuario,
            @Param("titulo") String titulo
    );
}
