package com.taller.crud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taller.crud.entity.Instructor;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByNombre(String nombre);

    Optional<Instructor> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Instructor> findByNombreContainingIgnoreCase(String nombre);

    List<Instructor> findByActivoTrue();

    List<Instructor> findByEspecialidad(String especialidad);

    Optional<Instructor> findByIdAndActivoTrue(Long id);
}