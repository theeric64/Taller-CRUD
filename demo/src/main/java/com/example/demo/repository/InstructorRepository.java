package com.example.demo.repository;

import Model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    
    Optional<Instructor> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}
