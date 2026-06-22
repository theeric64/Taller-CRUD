package com.example.demo.repository;

import Model.Reserva;
import Model.Instructor;
import Model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    /**
     * Cuenta las reservas ACTIVAS de un instructor en una fecha específica.
     * Valida si hay solapamiento de horarios entre la fecha de inicio y fin.
     * 
     * @param instructor El instructor a consultar
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return El número de reservas activas que solapan con el rango de fechas
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.instructor = :instructor " +
           "AND r.estado = 'ACTIVA' " +
           "AND r.fechaInicio < :fechaFin " +
           "AND r.fechaFin > :fechaInicio")
    long countReservasActivasPorInstructor(
        @Param("instructor") Instructor instructor,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Cuenta las reservas ACTIVAS de un instructor EN UN DÍA ESPECÍFICO.
     * Solo cuenta las que ocurren exactamente en ese día.
     * 
     * @param instructor El instructor a consultar
     * @param fechaInicio Fecha de inicio del día
     * @param fechaFin Fecha de fin del día
     * @return El número de reservas activas en ese día
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.instructor = :instructor " +
           "AND r.estado = 'ACTIVA' " +
           "AND DATE(r.fechaInicio) = DATE(:fecha)")
    long countReservasActivasDelDia(
        @Param("instructor") Instructor instructor,
        @Param("fecha") LocalDateTime fecha
    );
}
