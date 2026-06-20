package Model;

@Entity
@Table(name = "reserva")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ambiente_id", nullable = false)
    private Ambiente ambiente;

    private String nombreInstructor;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private int numeroAprendices;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.ACTIVA;
}
