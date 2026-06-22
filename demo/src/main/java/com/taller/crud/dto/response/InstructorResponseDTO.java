package com.taller.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String especialidad;
    private Integer aniosExperiencia;
    private Boolean activo;
}