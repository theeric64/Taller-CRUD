package com.taller.crud.controller;

import com.taller.crud.dto.request.InstructorRequestDTO;
import com.taller.crud.dto.response.InstructorResponseDTO;
import com.taller.crud.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/instructores")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    /**
     * Crea un nuevo instructor.
     *
     * @param dto Datos del instructor a crear
     * @return 201 Created con el instructor creado y la URI del recurso
     */
    @PostMapping
    public ResponseEntity<InstructorResponseDTO> crear(@Valid @RequestBody InstructorRequestDTO dto) {
        InstructorResponseDTO response = instructorService.crear(dto);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Obtiene todos los instructores.
     *
     * @return 200 OK con la lista de instructores
     */
    @GetMapping
    public ResponseEntity<List<InstructorResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(instructorService.obtenerTodos());
    }

    /**
     * Obtiene un instructor por su ID.
     *
     * @param id ID del instructor
     * @return 200 OK con el instructor encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.obtenerPorId(id));
    }

    /**
     * Actualiza un instructor existente.
     *
     * @param id  ID del instructor a actualizar
     * @param dto Nuevos datos del instructor
     * @return 200 OK con el instructor actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InstructorRequestDTO dto) {
        return ResponseEntity.ok(instructorService.actualizar(id, dto));
    }

    /**
     * Elimina un instructor.
     *
     * @param id ID del instructor a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        instructorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}