package com.taller.crud.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExcepciónNegocio {

    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String recurso, Long id) {
            super(String.format("%s no encontrado con ID: %d", recurso, id));
        }
    }

    public static class EmailDuplicadoException extends RuntimeException {
        public EmailDuplicadoException(String email) {
            super("Ya existe un usuario registrado con el email: " + email);
        }
    }

    public static class LimiteReservasExcedidoException extends RuntimeException {
        public LimiteReservasExcedidoException(Long instructorId, LocalDate fecha) {
            super(String.format(
                "El instructor con ID %d ya tiene 3 reservas activas para el día %s. No puede tener más reservas el mismo día.",
                instructorId, fecha
            ));
        }
    }

    public static class AmbienteOcupadoException extends RuntimeException {
        public AmbienteOcupadoException(Long ambienteId, LocalDateTime inicio, LocalDateTime fin) {
            super(String.format(
                "El ambiente con ID %d ya está ocupado en el horario %s - %s",
                ambienteId, inicio, fin
            ));
        }
    }

    public static class ReservaCanceladaException extends RuntimeException {
        public ReservaCanceladaException(Long id) {
            super("No se puede modificar la reserva con ID " + id + " porque ya está cancelada");
        }
    }

    public static class ValidacionNegocioException extends RuntimeException {
        public ValidacionNegocioException(String message) {
            super(message);
        }
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarEmailDuplicado(EmailDuplicadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(LimiteReservasExcedidoException.class)
    public ResponseEntity<ErrorResponse> manejarLimiteReservas(LimiteReservasExcedidoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AmbienteOcupadoException.class)
    public ResponseEntity<ErrorResponse> manejarAmbienteOcupado(AmbienteOcupadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ReservaCanceladaException.class)
    public ResponseEntity<ErrorResponse> manejarReservaCancelada(ReservaCanceladaException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ValidacionNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarValidacionNegocio(ValidacionNegocioException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validación en los datos enviados")
                .errors(errores)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Error interno del servidor: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(HttpStatus status, String mensaje) {
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .message(mensaje)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {

        private int status;
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;
    }
}