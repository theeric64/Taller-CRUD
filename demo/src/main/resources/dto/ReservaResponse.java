package dto;

import java.time.LocalDateTime;

import Model.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponse {

    private Long id;
    private Long ambienteId;
    private String nombreAmbiente;
    private Long instructorId;
    private String nombreInstructor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int numeroAprendices;
    private EstadoReserva estado;
}
