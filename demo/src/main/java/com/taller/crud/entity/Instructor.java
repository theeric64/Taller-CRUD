package com.taller.crud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 100)
    private String especialidad;

    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    public Instructor(String nombre, String email, String especialidad, Integer aniosExperiencia) {
        this.nombre = nombre;
        this.email = email;
        this.especialidad = especialidad;
        this.aniosExperiencia = aniosExperiencia;
        this.activo = true;
    }
}