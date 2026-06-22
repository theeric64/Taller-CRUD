package com.example.demo.controller;

import com.example.demo.service.InstructorService;
import dto.InstructorRequest;
import dto.InstructorResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/instructores")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    /**
     * POST /api/instructores
     * Crea un nuevo instructor
     * 
     * @param instructorRequest Los datos del instructor
     * @return 201 Created con el instructor creado
     * @return 400 Bad Request si los datos son inválidos o ya existe uno con ese nombre
     */
    @PostMapping
    public ResponseEntity<InstructorResponse> crearInstructor(@Valid @RequestBody InstructorRequest instructorRequest) {
        InstructorResponse instructor = instructorService.crearInstructor(instructorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(instructor);
    }

    /**
     * GET /api/instructores/{id}
     * Obtiene un instructor por ID
     * 
     * @param id El ID del instructor
     * @return 200 OK con el instructor
     * @return 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> obtenerInstructor(@PathVariable Long id) {
        InstructorResponse instructor = instructorService.obtenerInstructor(id);
        return ResponseEntity.ok(instructor);
    }

    /**
     * GET /api/instructores
     * Obtiene todos los instructores
     * 
     * @return 200 OK con la lista de instructores
     */
    @GetMapping
    public ResponseEntity<List<InstructorResponse>> obtenerTodosLosInstructores() {
        List<InstructorResponse> instructores = instructorService.obtenerTodosLosInstructores();
        return ResponseEntity.ok(instructores);
    }

    /**
     * PUT /api/instructores/{id}
     * Actualiza los datos de un instructor
     * 
     * @param id El ID del instructor
     * @param instructorRequest Los nuevos datos
     * @return 200 OK con el instructor actualizado
     * @return 404 Not Found si no existe
     * @return 400 Bad Request si los datos son inválidos
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> actualizarInstructor(
            @PathVariable Long id,
            @Valid @RequestBody InstructorRequest instructorRequest) {
        InstructorResponse instructor = instructorService.actualizarInstructor(id, instructorRequest);
        return ResponseEntity.ok(instructor);
    }

    /**
     * DELETE /api/instructores/{id}
     * Elimina un instructor
     * 
     * @param id El ID del instructor
     * @return 204 No Content
     * @return 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInstructor(@PathVariable Long id) {
        instructorService.eliminarInstructor(id);
        return ResponseEntity.noContent().build();
    }
}
