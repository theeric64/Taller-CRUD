package dto;

@Data
public class ReservaRequest {

    @NotNull
    private Long ambienteId;        

    @NotBlank
    private String nombreInstructor;

    @NotNull
    private LocalDateTime fechaInicio; 

    @NotNull
    private LocalDateTime fechaFin;

    @Min(1)
    private int numeroAprendices;
}