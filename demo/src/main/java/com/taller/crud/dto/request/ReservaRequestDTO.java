package com.taller.crud.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El ID del ambiente es obligatorio.")
    private Long ambienteId;

    @NotNull(message = "El ID del instructor es obligatorio.")
    private Long instructorId;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @Future(message = "La fecha de inicio debe ser futura.")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria.")
    @Future(message = "La fecha de fin debe ser futura.")
    private LocalDateTime fechaFin;

    @Min(value = 1, message = "Debe haber al menos 1 aprendiz.")
    private Integer numeroAprendices;

    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio.")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return fechaFin.isAfter(fechaInicio);
    }
}