package dto;

@Data
public class AmbienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private TipoAmbiente tipo;  

    @Min(1)
    private int capacidad;

    private boolean activo = true;
}
