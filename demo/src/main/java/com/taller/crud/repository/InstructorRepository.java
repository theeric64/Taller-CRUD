package com.taller.crud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taller.crud.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    
    // Buscar por nombre exacto
    Optional<Instructor> findByNombre(String nombre);
    
    // Buscar por email (único)
    Optional<Instructor> findByEmail(String email);
    
    // Verificar si existe por email
    boolean existsByEmail(String email);
    
    // Buscar por nombre (ignorando mayúsculas/minúsculas)
    List<Instructor> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar instructores activos
    List<Instructor> findByActivoTrue();
    
    // Buscar por especialidad
    List<Instructor> findByEspecialidad(String especialidad);
    
    // Buscar instructor por ID y que esté activo
    Optional<Instructor> findByIdAndActivoTrue(Long id);
}