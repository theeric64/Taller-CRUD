package com.taller.crud.service;

import com.taller.crud.dto.response.AmbienteDTO;
import com.taller.crud.entity.Ambiente;
import com.taller.crud.exception.ExcepciónNegocio;
import com.taller.crud.repository.AmbienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// ============ INTERFAZ ============
public interface AmbienteService {
    List<AmbienteDTO> obtenerTodos();
    AmbienteDTO obtenerPorId(Long id);
    List<AmbienteDTO> obtenerActivos();
}

// ============ IMPLEMENTACIÓN ============
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AmbienteServiceImpl implements AmbienteService {

    private final AmbienteRepository ambienteRepository;

    @Override
    public List<AmbienteDTO> obtenerTodos() {
        return ambienteRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public AmbienteDTO obtenerPorId(Long id) {
        Ambiente ambiente = ambienteRepository.findById(id)
                .orElseThrow(() -> new ExcepciónNegocio.RecursoNoEncontradoException("Ambiente", id));
        return convertirADTO(ambiente);
    }

    @Override
    public List<AmbienteDTO> obtenerActivos() {
        return ambienteRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private AmbienteDTO convertirADTO(Ambiente ambiente) {
        return AmbienteDTO.builder()
                .id(ambiente.getId())
                .nombre(ambiente.getNombre())
                .tipo(ambiente.getTipo().name())
                .capacidad(ambiente.getCapacidad())
                .activo(ambiente.getActivo())
                .build();
    }
}
