package com.taller.crud.service;

import com.taller.crud.dto.request.ReservaRequestDTO;
import com.taller.crud.dto.response.AmbienteDTO;
import com.taller.crud.dto.response.InstructorResponseDTO;
import com.taller.crud.dto.response.ReservaResponseDTO;
import com.taller.crud.entity.Ambiente;
import com.taller.crud.entity.EstadoReserva;
import com.taller.crud.entity.Instructor;
import com.taller.crud.entity.Reserva;
import com.taller.crud.exception.GlobalExceptionHandler;
import com.taller.crud.repository.AmbienteRepository;
import com.taller.crud.repository.InstructorRepository;
import com.taller.crud.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public interface ReservaService {
    ReservaResponseDTO crear(ReservaRequestDTO dto);
    ReservaResponseDTO obtenerPorId(Long id);
    List<ReservaResponseDTO> obtenerTodas(Long instructorId, Long ambienteId, LocalDate fecha);
    ReservaResponseDTO actualizar(Long id, ReservaRequestDTO dto);
    void cancelar(Long id);
    List<AmbienteDTO> obtenerDisponibilidad(LocalDate fecha);
}

@Service
@RequiredArgsConstructor
@Transactional
class ReservaServiceImpl implements ReservaService {

    private static final LocalTime HORA_APERTURA = LocalTime.of(6, 0);   
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);    
    private static final int DURACION_MINIMA_HORAS = 1;
    private static final int DURACION_MAXIMA_HORAS = 4;
    private static final int MAX_RESERVAS_POR_DIA = 3;
    private static final int HORAS_MINIMAS_PARA_CANCELAR = 2;

    private final ReservaRepository reservaRepository;
    private final InstructorRepository instructorRepository;
    private final AmbienteRepository ambienteRepository;

    @Override
    public ReservaResponseDTO crear(ReservaRequestDTO dto) {

        if (dto.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                "No se puede reservar en el pasado. La fecha de inicio debe ser futura."
            );
        }

        LocalTime horaInicio = dto.getFechaInicio().toLocalTime();
        LocalTime horaFin = dto.getFechaFin().toLocalTime();

        if (horaInicio.isBefore(HORA_APERTURA) || horaFin.isAfter(HORA_CIERRE)) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                String.format("El horario permitido es de %s a %s. " +
                    "Tu reserva es de %s a %s.",
                    HORA_APERTURA, HORA_CIERRE, horaInicio, horaFin)
            );
        }

        if (!dto.getFechaInicio().toLocalDate().equals(dto.getFechaFin().toLocalDate())) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                "La reserva debe ser en el mismo día. No se permiten reservas que crucen días."
            );
        }

        Duration duracion = Duration.between(dto.getFechaInicio(), dto.getFechaFin());
        long horas = duracion.toHours();
        long minutos = duracion.toMinutes() % 60;

        if (duracion.toMinutes() < 60) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                String.format("La reserva debe durar al menos %d hora. " +
                    "Tu reserva dura %d horas y %d minutos.",
                    DURACION_MINIMA_HORAS, horas, minutos)
            );
        }

        if (duracion.toHours() > DURACION_MAXIMA_HORAS || 
            (duracion.toHours() == DURACION_MAXIMA_HORAS && duracion.toMinutes() % 60 > 0)) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                String.format("La reserva no puede durar más de %d horas. " +
                    "Tu reserva dura %d horas y %d minutos.",
                    DURACION_MAXIMA_HORAS, horas, minutos)
            );
        }

        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new GlobalExceptionHandler.RecursoNoEncontradoException(
                        "Instructor", dto.getInstructorId()));

        if (!instructor.getActivo()) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                    "El instructor " + instructor.getNombre() + " no está activo. " +
                    "No puede realizar reservas."
            );
        }

        Ambiente ambiente = ambienteRepository.findById(dto.getAmbienteId())
                .orElseThrow(() -> new GlobalExceptionHandler.RecursoNoEncontradoException(
                        "Ambiente", dto.getAmbienteId()));

        if (!ambiente.getActivo()) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                    "El ambiente " + ambiente.getNombre() + " no está activo. " +
                    "No se pueden hacer reservas en este ambiente."
            );
        }

        if (dto.getNumeroAprendices() != null && dto.getNumeroAprendices() > ambiente.getCapacidad()) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                String.format("El número de aprendices (%d) supera la capacidad del ambiente %s (%d personas).",
                    dto.getNumeroAprendices(), ambiente.getNombre(), ambiente.getCapacidad())
            );
        }

        long reservasDelDia = reservaRepository.countByInstructorIdAndFechaAndEstadoActiva(
                dto.getInstructorId(),
                dto.getFechaInicio().toLocalDate());

        if (reservasDelDia >= MAX_RESERVAS_POR_DIA) {
            throw new GlobalExceptionHandler.LimiteReservasExcedidoException(
                    dto.getInstructorId(),
                    dto.getFechaInicio().toLocalDate());
        }

        List<Reserva> reservasSolapadas = reservaRepository
                .findByAmbienteIdAndFechaInicioLessThanAndFechaFinGreaterThanAndEstado(
                        dto.getAmbienteId(),
                        dto.getFechaFin(),
                        dto.getFechaInicio(),
                        EstadoReserva.ACTIVA);

        if (!reservasSolapadas.isEmpty()) {
            Reserva conflicto = reservasSolapadas.get(0);
            throw new GlobalExceptionHandler.AmbienteOcupadoException(
                    dto.getAmbienteId(),
                    conflicto.getFechaInicio(),
                    conflicto.getFechaFin());
        }

        Reserva reserva = new Reserva();
        reserva.setInstructor(instructor);
        reserva.setAmbiente(ambiente);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setNumeroAprendices(dto.getNumeroAprendices());
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setFechaCreacion(LocalDateTime.now());

        reserva = reservaRepository.save(reserva);
        return convertirADTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.RecursoNoEncontradoException("Reserva", id));
        return convertirADTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodas(Long instructorId, Long ambienteId, LocalDate fecha) {
        return reservaRepository.findByFiltros(instructorId, ambienteId, fecha)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservaResponseDTO actualizar(Long id, ReservaRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.RecursoNoEncontradoException("Reserva", id));

        if (EstadoReserva.CANCELADA.equals(reserva.getEstado())) {
            throw new GlobalExceptionHandler.ReservaCanceladaException(id);
        }

        if (EstadoReserva.FINALIZADA.equals(reserva.getEstado())) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                "No se puede modificar una reserva que ya finalizó."
            );
        }
        return crear(dto);
    }

    @Override
    public void cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.RecursoNoEncontradoException("Reserva", id));

        if (EstadoReserva.CANCELADA.equals(reserva.getEstado())) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                    "La reserva ya está cancelada.");
        }

        if (EstadoReserva.FINALIZADA.equals(reserva.getEstado())) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                    "No se puede cancelar una reserva que ya finalizó.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioReserva = reserva.getFechaInicio();
        Duration tiempoHastaInicio = Duration.between(ahora, inicioReserva);

        if (tiempoHastaInicio.toHours() < HORAS_MINIMAS_PARA_CANCELAR) {
            throw new GlobalExceptionHandler.ValidacionNegocioException(
                String.format("No se puede cancelar la reserva. " +
                    "Debe cancelarse al menos %d horas antes del inicio. " +
                    "Faltan %d horas y %d minutos para que inicie.",
                    HORAS_MINIMAS_PARA_CANCELAR,
                    tiempoHastaInicio.toHours(),
                    tiempoHastaInicio.toMinutes() % 60)
            );
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmbienteDTO> obtenerDisponibilidad(LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();

        List<Ambiente> ambientesOcupados = reservaRepository
                .findAmbientesOcupadosEnFecha(inicioDia, finDia, EstadoReserva.ACTIVA);

        return ambienteRepository.findByActivoTrue()
                .stream()
                .map(ambiente -> AmbienteDTO.builder()
                        .id(ambiente.getId())
                        .nombre(ambiente.getNombre())
                        .tipo(ambiente.getTipo().name())
                        .capacidad(ambiente.getCapacidad())
                        .activo(ambiente.getActivo())
                        .disponible(!ambientesOcupados.contains(ambiente))
                        .build())
                .collect(Collectors.toList());
    }

    private ReservaResponseDTO convertirADTO(Reserva reserva) {
        
        InstructorResponseDTO instructorDTO = InstructorResponseDTO.builder()
                .id(reserva.getInstructor().getId())
                .nombre(reserva.getInstructor().getNombre())
                .email(reserva.getInstructor().getEmail())
                .especialidad(reserva.getInstructor().getEspecialidad())
                .aniosExperiencia(reserva.getInstructor().getAniosExperiencia())
                .activo(reserva.getInstructor().getActivo())
                .build();

        AmbienteDTO ambienteDTO = AmbienteDTO.builder()
                .id(reserva.getAmbiente().getId())
                .nombre(reserva.getAmbiente().getNombre())
                .tipo(reserva.getAmbiente().getTipo().name())
                .capacidad(reserva.getAmbiente().getCapacidad())
                .activo(reserva.getAmbiente().getActivo())
                .build();

        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .instructor(instructorDTO)
                .ambiente(ambienteDTO)
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .numeroAprendices(reserva.getNumeroAprendices())
                .estado(reserva.getEstado().name())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
}