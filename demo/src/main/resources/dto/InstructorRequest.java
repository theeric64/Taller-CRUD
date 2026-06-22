package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorRequest {

    @NotBlank(message = "El nombre del instructor es obligatorio")
    private String nombre;

    @Email(message = "El email debe ser válido")
    private String email;

    private boolean activo = true;
}
