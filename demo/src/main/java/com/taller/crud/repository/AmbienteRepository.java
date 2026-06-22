package com.taller.crud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taller.crud.entity.Ambiente;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
    
    // Buscar por nombre exacto
    Optional<Ambiente> findByNombre(String nombre);
    
    // Buscar por nombre (ignorando mayúsculas/minúsculas)
    Optional<Ambiente> findByNombreIgnoreCase(String nombre);
    
    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
    
    // Buscar solo ambientes activos
    List<Ambiente> findByActivoTrue();
    
    // Buscar por tipo de ambiente
    List<Ambiente> findByTipo(com.taller.crud.entity.TipoAmbiente tipo);
    
    // Buscar ambiente por ID y que esté activo
    Optional<Ambiente> findByIdAndActivoTrue(Long id);
    
    // Contar ambientes por tipo
    long countByTipo(com.taller.crud.entity.TipoAmbiente tipo);
}