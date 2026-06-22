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

    List<Reserva> findByInstructorId(Long instructorId);

    List<Reserva> findByAmbienteId(Long ambienteId);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByInstructorIdAndEstado(Long instructorId, EstadoReserva estado);

    List<Reserva> findByAmbienteIdAndFechaInicioLessThanAndFechaFinGreaterThanAndEstado(
            Long ambienteId, 
            LocalDateTime fechaFin, 
            LocalDateTime fechaInicio, 
            EstadoReserva estado
    );

    @Query("SELECT COUNT(r) FROM Reserva r " +
           "WHERE r.instructor.id = :instructorId " +
           "AND r.estado = 'ACTIVA' " +
           "AND FUNCTION('DATE', r.fechaInicio) = :fecha")
    long countByInstructorIdAndFechaAndEstadoActiva(
            @Param("instructorId") Long instructorId,
            @Param("fecha") LocalDate fecha
    );

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

    @Query("SELECT DISTINCT r.ambiente FROM Reserva r " +
           "WHERE r.fechaInicio >= :inicioDia " +
           "AND r.fechaInicio < :finDia " +
           "AND r.estado = :estado")
    List<com.taller.crud.entity.Ambiente> findAmbientesOcupadosEnFecha(
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia,
            @Param("estado") EstadoReserva estado
    );

    @Query("SELECT r FROM Reserva r " +
           "WHERE (:instructorId IS NULL OR r.instructor.id = :instructorId) " +
           "AND (:ambienteId IS NULL OR r.ambiente.id = :ambienteId) " +
           "AND (:fecha IS NULL OR FUNCTION('DATE', r.fechaInicio) = :fecha)")
    List<Reserva> findByFiltros(
            @Param("instructorId") Long instructorId,
            @Param("ambienteId") Long ambienteId,
            @Param("fecha") LocalDate fecha
    );

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.instructor " +
           "JOIN FETCH r.ambiente " +
           "WHERE r.id = :id")
    java.util.Optional<Reserva> findByIdWithRelations(@Param("id") Long id);
}