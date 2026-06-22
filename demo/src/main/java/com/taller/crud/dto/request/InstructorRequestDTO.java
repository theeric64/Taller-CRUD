package com.taller.crud.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorRequestDTO {

    @NotBlank(message = "El nombre del instructor es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String especialidad;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    private Integer aniosExperiencia;
}