package com.taller.crud.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ambientes")  // Plural en DB (buena práctica)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAmbiente tipo;

    @Column(nullable = false)
    private Integer capacidad;  // Integer en vez de int

    @Builder.Default
    private Boolean activo = true;  // Boolean en vez de boolean, valor por defecto

    // Constructor personalizado para JPA (sin id)
    public Ambiente(String nombre, TipoAmbiente tipo, Integer capacidad) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.activo = true;
    }
}