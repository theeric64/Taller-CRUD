package com.taller.crud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taller.crud.entity.Ambiente;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

    Optional<Ambiente> findByNombre(String nombre);

    Optional<Ambiente> findByNombreIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);

    List<Ambiente> findByActivoTrue();

    List<Ambiente> findByTipo(com.taller.crud.entity.TipoAmbiente tipo);

    Optional<Ambiente> findByIdAndActivoTrue(Long id);

    long countByTipo(com.taller.crud.entity.TipoAmbiente tipo);
}