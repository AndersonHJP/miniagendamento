package dev.andersonhjp.miniagendamento.repository;

import dev.andersonhjp.miniagendamento.model.Agendamento;
import dev.andersonhjp.miniagendamento.model.StatusAgendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
                           @Param("inicio") LocalDateTime inicio,
                           @Param("fim") LocalDateTime fim,
                           @Param("ignoreId") Long ignoreId);


    @Query("""
                SELECT a FROM Agendamento a
            WHERE (:status IS NULL OR a.status = :status)
              AND (CAST(:dataInicio AS timestamp) IS NULL OR a.dataFim >= :dataInicio)
              AND (CAST(:dataFim AS timestamp) IS NULL OR a.dataInicio <= :dataFim)
              AND (:usuario IS NULL OR a.usuario = :usuario)
              AND LOWER(a.titulo) LIKE LOWER(CONCAT('%', COALESCE(:titulo, ''), '%'))
            """)
    Page<Agendamento> buscarComFiltro(
            @Param("status") StatusAgendamento status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("usuario") String usuario,
            @Param("titulo") String titulo,
            Pageable pageable
    );
}
