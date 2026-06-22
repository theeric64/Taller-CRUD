package com.taller.crud.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

    private Long id;
    private Long instructorId;
    private String nombreInstructor;
    private Long ambienteId;
    private String nombreAmbiente;
    private String tipoAmbiente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private LocalDateTime fechaCreacion;
}