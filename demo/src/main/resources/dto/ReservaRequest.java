package dto;

@Data
public class ReservaRequest {

    @NotNull(message = "El ID del ambiente es obligatorio")
    private Long ambienteId;        

    @NotNull(message = "El ID del instructor es obligatorio")
    private Long instructorId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio; 

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @Min(value = 1, message = "Debe haber al menos 1 aprendiz")
    private int numeroAprendices;
}