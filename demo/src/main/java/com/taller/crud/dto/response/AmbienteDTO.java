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
    private String tipo;          // String, no la entidad TipoAmbiente
    private Integer capacidad;    // Integer en vez de int (puede ser null)
    private Boolean activo;       // Boolean en vez de boolean
    private Boolean disponible;   // Campo útil para el endpoint de disponibilidad
}   