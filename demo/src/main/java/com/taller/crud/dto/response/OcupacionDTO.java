package com.taller.crud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcupacionDTO {

    private Long ambienteId;
    private String nombreAmbiente;
    private Double horasReservadas;
    private Double porcentajeOcupacion;
}