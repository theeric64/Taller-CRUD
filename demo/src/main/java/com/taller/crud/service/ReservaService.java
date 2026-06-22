package com.example.demo.service;

import Model.Reserva;
import Model.Instructor;
import Model.Ambiente;
import Model.EstadoReserva;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.InstructorRepository;
import com.example.demo.repository.AmbienteRepository;
import dto.ReservaRequest;
import dto.ReservaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AmbienteRepository ambienteRepository;

    /**
     * Crea una nueva reserva validando:
     * 1. Que el instructor exista en la base de datos
     * 2. Que el ambiente exista en la base de datos
     * 3. Que el instructor no tenga más de 3 reservas ACTIVAS en el mismo día
     * 4. Que la fecha de inicio sea anterior a la fecha de fin
     * 
     * @param reservaRequest La solicitud de creación de reserva
     * @return La reserva creada con la respuesta DTO
     * @throws ResponseStatusException 404 si instructor o ambiente no existen
     * @throws ResponseStatusException 409 si el instructor ya tiene 3 reservas activas ese día
     * @throws ResponseStatusException 400 si las fechas son inválidas
     */
    public ReservaResponse crearReserva(ReservaRequest reservaRequest) {
        
        // Validar que las fechas sean válidas
        if (reservaRequest.getFechaInicio().isAfter(reservaRequest.getFechaFin())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha de inicio debe ser anterior a la fecha de fin"
            );
        }

        if (reservaRequest.getFechaInicio().isEqual(reservaRequest.getFechaFin())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha de inicio y fin no pueden ser iguales"
            );
        }

        // Buscar y validar que el instructor exista
        Instructor instructor = instructorRepository.findById(reservaRequest.getInstructorId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Instructor con ID " + reservaRequest.getInstructorId() + " no encontrado"
            ));

        // Validar que el instructor esté activo
        if (!instructor.isActivo()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El instructor " + instructor.getNombre() + " no está activo"
            );
        }

        // Buscar y validar que el ambiente exista
        Ambiente ambiente = ambienteRepository.findById(reservaRequest.getAmbienteId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ambiente con ID " + reservaRequest.getAmbienteId() + " no encontrado"
            ));

        // Validar que el ambiente esté activo
        if (!ambiente.isActivo()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El ambiente " + ambiente.getNombre() + " no está activo"
            );
        }

        // Validar la regla de negocio: máximo 3 reservas ACTIVAS por instructor el mismo día
        long reservasActivasDelDia = reservaRepository.countReservasActivasDelDia(
            instructor,
            reservaRequest.getFechaInicio()
        );

        if (reservasActivasDelDia >= 3) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El instructor " + instructor.getNombre() + 
                " ya tiene 3 reservas activas el " + 
                reservaRequest.getFechaInicio().toLocalDate() +
                ". No puede tener más reservas el mismo día"
            );
        }

        // Crear la nueva reserva
        Reserva reserva = Reserva.builder()
            .instructor(instructor)
            .ambiente(ambiente)
            .fechaInicio(reservaRequest.getFechaInicio())
            .fechaFin(reservaRequest.getFechaFin())
            .numeroAprendices(reservaRequest.getNumeroAprendices())
            .estado(EstadoReserva.ACTIVA)
            .build();

        Reserva reservaGuardada = reservaRepository.save(reserva);

        return mapToResponse(reservaGuardada);
    }

    /**
     * Obtiene una reserva por ID
     * 
     * @param id El ID de la reserva
     * @return La reserva encontrada
     * @throws ResponseStatusException 404 si no se encuentra
     */
    public ReservaResponse obtenerReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Reserva con ID " + id + " no encontrada"
            ));
        
        return mapToResponse(reserva);
    }

    /**
     * Obtiene todas las reservas
     * 
     * @return Lista de todas las reservas
     */
    public List<ReservaResponse> obtenerTodasLasReservas() {
        return reservaRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de una reserva
     * 
     * @param id El ID de la reserva
     * @param nuevoEstado El nuevo estado
     * @return La reserva actualizada
     * @throws ResponseStatusException 404 si no se encuentra
     */
    public ReservaResponse actualizarEstadoReserva(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Reserva con ID " + id + " no encontrada"
            ));
        
        reserva.setEstado(nuevoEstado);
        Reserva reservaActualizada = reservaRepository.save(reserva);
        
        return mapToResponse(reservaActualizada);
    }

    /**
     * Elimina una reserva
     * 
     * @param id El ID de la reserva
     * @throws ResponseStatusException 404 si no se encuentra
     */
    public void eliminarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Reserva con ID " + id + " no encontrada"
            ));
        
        reservaRepository.delete(reserva);
    }

    /**
     * Convierte una entidad Reserva a ReservaResponse DTO
     */
    private ReservaResponse mapToResponse(Reserva reserva) {
        return ReservaResponse.builder()
            .id(reserva.getId())
            .ambienteId(reserva.getAmbiente().getId())
            .nombreAmbiente(reserva.getAmbiente().getNombre())
            .instructorId(reserva.getInstructor().getId())
            .nombreInstructor(reserva.getInstructor().getNombre())
            .fechaInicio(reserva.getFechaInicio())
            .fechaFin(reserva.getFechaFin())
            .numeroAprendices(reserva.getNumeroAprendices())
            .estado(reserva.getEstado())
            .build();
    }
}
