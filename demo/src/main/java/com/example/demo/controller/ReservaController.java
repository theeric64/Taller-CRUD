package com.example.demo.controller;

import com.example.demo.service.ReservaService;
import dto.ReservaRequest;
import dto.ReservaResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    /**
     * POST /api/reservas
     * Crea una nueva reserva
     * 
     * Validaciones:
     * - El instructor debe existir y estar activo
     * - El ambiente debe existir y estar activo
     * - El instructor no puede tener más de 3 reservas ACTIVAS el mismo día
     * - Las fechas deben ser válidas (inicio < fin)
     * 
     * @param reservaRequest Los datos de la reserva a crear
     * @return 201 Created con la reserva creada
     * @return 404 Not Found si instructor o ambiente no existen
     * @return 409 Conflict si el instructor ya tiene 3 reservas activas ese día
     * @return 400 Bad Request si los datos son inválidos
     */
    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(@Valid @RequestBody ReservaRequest reservaRequest) {
        ReservaResponse reserva = reservaService.crearReserva(reservaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    /**
     * GET /api/reservas/{id}
     * Obtiene una reserva por ID
     * 
     * @param id El ID de la reserva
     * @return 200 OK con la reserva
     * @return 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerReserva(@PathVariable Long id) {
        ReservaResponse reserva = reservaService.obtenerReserva(id);
        return ResponseEntity.ok(reserva);
    }

    /**
     * GET /api/reservas
     * Obtiene todas las reservas
     * 
     * @return 200 OK con la lista de reservas
     */
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> obtenerTodasLasReservas() {
        List<ReservaResponse> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    /**
     * PUT /api/reservas/{id}
     * Actualiza una reserva (no implementado en esta versión)
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest reservaRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Actualización de reservas aún no implementada");
    }

    /**
     * DELETE /api/reservas/{id}
     * Elimina una reserva (marca como CANCELADA)
     * 
     * @param id El ID de la reserva
     * @return 204 No Content
     * @return 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
