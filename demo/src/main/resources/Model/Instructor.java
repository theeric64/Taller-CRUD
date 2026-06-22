package Model;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "instructor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del instructor es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String email;

    private boolean activo = true;
}
