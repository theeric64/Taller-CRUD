package com.example.demo.service;

import Model.Instructor;
import com.example.demo.repository.InstructorRepository;
import dto.InstructorRequest;
import dto.InstructorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    /**
     * Crea un nuevo instructor
     * 
     * @param instructorRequest Los datos del instructor
     * @return El instructor creado
     * @throws ResponseStatusException 400 si el nombre ya existe
     */
    public InstructorResponse crearInstructor(InstructorRequest instructorRequest) {
        
        // Validar que no exista un instructor con el mismo nombre
        if (instructorRepository.existsByNombre(instructorRequest.getNombre())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un instructor con el nombre: " + instructorRequest.getNombre()
            );
        }

        Instructor instructor = Instructor.builder()
            .nombre(instructorRequest.getNombre())
            .email(instructorRequest.getEmail())
            .activo(instructorRequest.isActivo())
            .build();

        Instructor instructorGuardado = instructorRepository.save(instructor);
        
        return mapToResponse(instructorGuardado);
    }

    /**
     * Obtiene un instructor por ID
     * 
     * @param id El ID del instructor
     * @return El instructor encontrado
     * @throws ResponseStatusException 404 si no se encuentra
     */
    public InstructorResponse obtenerInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Instructor con ID " + id + " no encontrado"
            ));
        
        return mapToResponse(instructor);
    }

    /**
     * Obtiene todos los instructores
     * 
     * @return Lista de todos los instructores
     */
    public List<InstructorResponse> obtenerTodosLosInstructores() {
        return instructorRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de un instructor
     * 
     * @param id El ID del instructor
     * @param instructorRequest Los nuevos datos
     * @return El instructor actualizado
     * @throws ResponseStatusException 404 si no se encuentra
     */
    public InstructorResponse actualizarInstructor(Long id, InstructorRequest instructorRequest) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Instructor con ID " + id + " no encontrado"
            ));

        // Validar nombre único (si cambió el nombre)
        if (!instructor.getNombre().equals(instructorRequest.getNombre()) &&
            instructorRepository.existsByNombre(instructorRequest.getNombre())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ya existe un instructor con el nombre: " + instructorRequest.getNombre()
            );
        }

        instructor.setNombre(instructorRequest.getNombre());
        instructor.setEmail(instructorRequest.getEmail());
        instructor.setActivo(instructorRequest.isActivo());

        Instructor instructorActualizado = instructorRepository.save(instructor);
        
        return mapToResponse(instructorActualizado);
    }

    /**
     * Elimina un instructor
     * 
     * @param id El ID del instructor
     * @throws ResponseStatusException 404 si no se encuentra
     */
    public void eliminarInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Instructor con ID " + id + " no encontrado"
            ));
        
        instructorRepository.delete(instructor);
    }

    /**
     * Convierte una entidad Instructor a InstructorResponse DTO
     */
    private InstructorResponse mapToResponse(Instructor instructor) {
        return InstructorResponse.builder()
            .id(instructor.getId())
            .nombre(instructor.getNombre())
            .email(instructor.getEmail())
            .activo(instructor.isActivo())
            .build();
    }
}
