package com.taller.crud.service;

import com.taller.crud.dto.request.ReservaRequestDTO;
import com.taller.crud.dto.response.AmbienteDTO;
import com.taller.crud.dto.response.InstructorResponseDTO;
import com.taller.crud.dto.response.ReservaResponseDTO;
import com.taller.crud.entity.Ambiente;
import com.taller.crud.entity.EstadoReserva;
import com.taller.crud.entity.Instructor;
import com.taller.crud.entity.Reserva;
import com.taller.crud.exception.ExcepciónNegocio;
import com.taller.crud.repository.AmbienteRepository;
import com.taller.crud.repository.InstructorRepository;
import com.taller.crud.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final ReservaRepository reservaRepository;
    private final InstructorRepository instructorRepository;
    private final AmbienteRepository ambienteRepository;

    @Override
    public ReservaResponseDTO crear(ReservaRequestDTO dto) {

        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException(
                        "Instructor", dto.getInstructorId()));

        if (!instructor.getActivo()) {
            throw new ExcepciónNegocio.ValidacionNegocioException(
                    "El instructor " + instructor.getNombre() + " no está activo");
        }

        Ambiente ambiente = ambienteRepository.findByIdAndActivoTrue(dto.getAmbienteId())
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException(
                        "Ambiente", dto.getAmbienteId()));

        long reservasDelDia = reservaRepository.countByInstructorIdAndFechaAndEstadoActiva(
                dto.getInstructorId(),
                dto.getFechaInicio().toLocalDate());

        if (reservasDelDia >= 3) {
            throw new ExcepciónNegocio.LimiteReservasExcedidoException(
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
            throw new ExcepciónNegocio.AmbienteOcupadoException(
                    dto.getAmbienteId(),
                    dto.getFechaInicio(),
                    dto.getFechaFin());
        }

        Reserva reserva = new Reserva();
        reserva.setInstructor(instructor);
        reserva.setAmbiente(ambiente);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setEstado(EstadoReserva.ACTIVA);
        reserva.setFechaCreacion(LocalDateTime.now());

        reserva = reservaRepository.save(reserva);
        return convertirADTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException("Reserva", id));
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
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException("Reserva", id));

        if (EstadoReserva.CANCELADA.equals(reserva.getEstado())) {
            throw new ExcepciónNegocio.ReservaCanceladaException(id);
        }

        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException(
                        "Instructor", dto.getInstructorId()));

        Ambiente ambiente = ambienteRepository.findById(dto.getAmbienteId())
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException(
                        "Ambiente", dto.getAmbienteId()));

        reserva.setInstructor(instructor);
        reserva.setAmbiente(ambiente);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());

        reserva = reservaRepository.save(reserva);
        return convertirADTO(reserva);
    }

    @Override
    public void cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException("Reserva", id));

        if (EstadoReserva.CANCELADA.equals(reserva.getEstado())) {
            throw new ExcepciónNegocio.ValidacionNegocioException(
                    "La reserva ya está cancelada");
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
                .instructorId(instructorDTO.getId())
                .ambienteId(ambienteDTO.getId())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .estado(reserva.getEstado().name())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
}