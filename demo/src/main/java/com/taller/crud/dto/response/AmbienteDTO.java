package com.taller.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmbienteDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private Integer capacidad;
    private Boolean activo;
    private Boolean disponible;
}   