package Model;

@Entity
@Table(name = "ambiente")
@Data              
@NoArgsConstructor 
@AllArgsConstructor
@Builder           
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Enumerated(EnumType.STRING) 
    private TipoAmbiente tipo;

    @Min(value = 1, message = "Capacidad mínima: 1")
    private int capacidad;

    private boolean activo;
}
