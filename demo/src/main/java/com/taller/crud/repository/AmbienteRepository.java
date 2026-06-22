package com.example.demo.repository;

import Model.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
    
    Optional<Ambiente> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}
