package com.taller.crud.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.crud.entity.EstadoReserva;
import com.taller.crud.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // ============ MÉTODOS DERIVADOS (Spring Data JPA) ============
    
    // Buscar reservas por instructor
    List<Reserva> findByInstructorId(Long instructorId);
    
    // Buscar reservas por ambiente
    List<Reserva> findByAmbienteId(Long ambienteId);
    
    // Buscar reservas por estado
    List<Reserva> findByEstado(EstadoReserva estado);
    
    // Buscar reservas por instructor y estado
    List<Reserva> findByInstructorIdAndEstado(Long instructorId, EstadoReserva estado);
    
    // Buscar reservas activas en un rango de fechas (para validar solapamiento)
    List<Reserva> findByAmbienteIdAndFechaInicioLessThanAndFechaFinGreaterThanAndEstado(
            Long ambienteId, 
            LocalDateTime fechaFin, 
            LocalDateTime fechaInicio, 
            EstadoReserva estado
    );

    // ============ CONSULTAS JPQL OPTIMIZADAS ============
    
    /**
     * Cuenta las reservas ACTIVAS de un instructor en un día específico.
     * Solo cuenta reservas donde la fecha de inicio está en ese día.
     */
    @Query("SELECT COUNT(r) FROM Reserva r " +
           "WHERE r.instructor.id = :instructorId " +
           "AND r.estado = 'ACTIVA' " +
           "AND FUNCTION('DATE', r.fechaInicio) = :fecha")
    long countByInstructorIdAndFechaAndEstadoActiva(
            @Param("instructorId") Long instructorId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Cuenta las reservas ACTIVAS de un instructor en un rango de fechas.
     * Valida si hay solapamiento de horarios.
     */
    @Query("SELECT COUNT(r) FROM Reserva r " +
           "WHERE r.instructor.id = :instructorId " +
           "AND r.estado = 'ACTIVA' " +
           "AND r.fechaInicio < :fechaFin " +
           "AND r.fechaFin > :fechaInicio")
    long countReservasSolapadas(
            @Param("instructorId") Long instructorId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Encuentra ambientes ocupados en una fecha específica.
     */
    @Query("SELECT DISTINCT r.ambiente FROM Reserva r " +
           "WHERE r.fechaInicio >= :inicioDia " +
           "AND r.fechaInicio < :finDia " +
           "AND r.estado = :estado")
    List<com.taller.crud.entity.Ambiente> findAmbientesOcupadosEnFecha(
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia,
            @Param("estado") EstadoReserva estado
    );

    /**
     * Búsqueda combinada por instructor, ambiente y fecha.
     */
    @Query("SELECT r FROM Reserva r " +
           "WHERE (:instructorId IS NULL OR r.instructor.id = :instructorId) " +
           "AND (:ambienteId IS NULL OR r.ambiente.id = :ambienteId) " +
           "AND (:fecha IS NULL OR FUNCTION('DATE', r.fechaInicio) = :fecha)")
    List<Reserva> findByFiltros(
            @Param("instructorId") Long instructorId,
            @Param("ambienteId") Long ambienteId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Busca reservas por ID con fetch de relaciones (evita LazyInitializationException).
     */
    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.instructor " +
           "JOIN FETCH r.ambiente " +
           "WHERE r.id = :id")
    java.util.Optional<Reserva> findByIdWithRelations(@Param("id") Long id);
}