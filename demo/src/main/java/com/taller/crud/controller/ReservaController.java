package com.taller.crud.controller;

import com.taller.crud.dto.request.ReservaRequestDTO;
import com.taller.crud.dto.response.AmbienteDTO;
import com.taller.crud.dto.response.ReservaResponseDTO;
import com.taller.crud.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    /**
     * Crea una nueva reserva con sus validaciones de negocio.
     *
     * @param dto Datos de la reserva a crear
     * @return 201 Created con la reserva creada y la URI del recurso
     */
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO response = reservaService.crear(dto);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Obtiene todas las reservas.
     * Permite filtrar opcionalmente por instructor, ambiente y fecha.
     *
     * @param instructorId (Opcional) ID del instructor para filtrar
     * @param ambienteId   (Opcional) ID del ambiente para filtrar
     * @param fecha        (Opcional) Fecha para filtrar reservas
     * @return 200 OK con la lista de reservas
     */
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas(
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) Long ambienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<ReservaResponseDTO> reservas = reservaService.obtenerTodas(instructorId, ambienteId, fecha);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Obtiene una reserva por su ID.
     *
     * @param id ID de la reserva
     * @return 200 OK con la reserva encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    /**
     * Actualiza una reserva existente.
     *
     * @param id  ID de la reserva a actualizar
     * @param dto Nuevos datos de la reserva
     * @return 200 OK con la reserva actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.ok(reservaService.actualizar(id, dto));
    }

    /**
     * Cancela una reserva (eliminación lógica).
     * La reserva se marca como CANCELADA en lugar de borrarse físicamente.
     *
     * @param id ID de la reserva a cancelar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Consulta la disponibilidad de ambientes para una fecha específica.
     *
     * @param fecha Fecha a consultar (formato ISO: YYYY-MM-DD)
     * @return 200 OK con lista de ambientes y su disponibilidad
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<AmbienteDTO>> consultarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(reservaService.obtenerDisponibilidad(fecha));
    }
}