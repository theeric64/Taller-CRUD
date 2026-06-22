package com.taller.crud.service;

import com.taller.crud.dto.request.InstructorRequestDTO;
import com.taller.crud.dto.response.InstructorResponseDTO;
import com.taller.crud.entity.Instructor;
import com.taller.crud.exception.ExcepciónNegocio;
import com.taller.crud.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public interface InstructorService {
    InstructorResponseDTO crear(InstructorRequestDTO dto);
    InstructorResponseDTO obtenerPorId(Long id);
    List<InstructorResponseDTO> obtenerTodos();
    InstructorResponseDTO actualizar(Long id, InstructorRequestDTO dto);
    void eliminar(Long id);
}

@Service
@RequiredArgsConstructor
@Transactional
class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;

    @Override
    public InstructorResponseDTO crear(InstructorRequestDTO dto) {

        if (instructorRepository.existsByEmail(dto.getEmail())) {
            throw new ExcepciónNegocio.EmailDuplicadoException(dto.getEmail());
        }

        Instructor instructor = new Instructor();
        instructor.setNombre(dto.getNombre());
        instructor.setEmail(dto.getEmail());
        instructor.setEspecialidad(dto.getEspecialidad());
        instructor.setAniosExperiencia(dto.getAniosExperiencia());
        instructor.setActivo(true);

        instructor = instructorRepository.save(instructor);
        return convertirADTO(instructor);
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorResponseDTO obtenerPorId(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException("Instructor", id));
        return convertirADTO(instructor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorResponseDTO> obtenerTodos() {
        return instructorRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public InstructorResponseDTO actualizar(Long id, InstructorRequestDTO dto) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException("Instructor", id));

        if (!instructor.getEmail().equals(dto.getEmail()) &&
                instructorRepository.existsByEmail(dto.getEmail())) {
            throw new ExcepciónNegocio.EmailDuplicadoException(dto.getEmail());
        }
        instructor.setNombre(dto.getNombre());
        instructor.setEmail(dto.getEmail());
        instructor.setEspecialidad(dto.getEspecialidad());
        instructor.setAniosExperiencia(dto.getAniosExperiencia());

        instructor = instructorRepository.save(instructor);
        return convertirADTO(instructor);
    }

    @Override
    public void eliminar(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new ExcepciónNegocio.RecursoNoEncontradoException("Instructor", id);
        }
        instructorRepository.deleteById(id);
    }

    private InstructorResponseDTO convertirADTO(Instructor instructor) {
        return InstructorResponseDTO.builder()
                .id(instructor.getId())
                .nombre(instructor.getNombre())
                .email(instructor.getEmail())
                .especialidad(instructor.getEspecialidad())
                .aniosExperiencia(instructor.getAniosExperiencia())
                .activo(instructor.getActivo())
                .build();
    }
}