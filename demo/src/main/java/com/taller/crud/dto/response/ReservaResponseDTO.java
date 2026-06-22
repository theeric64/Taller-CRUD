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
    private InstructorResponseDTO instructor;
    private AmbienteDTO ambiente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer numeroAprendices;
    private String estado;
    private LocalDateTime fechaCreacion;
}