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

    @GetMapping
    public ResponseEntity<List<InstructorResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(instructorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InstructorRequestDTO dto) {
        return ResponseEntity.ok(instructorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        instructorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}