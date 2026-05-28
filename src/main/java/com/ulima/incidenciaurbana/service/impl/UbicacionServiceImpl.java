package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.UbicacionDTO;
import com.ulima.incidenciaurbana.model.Ubicacion;
import com.ulima.incidenciaurbana.repository.UbicacionRepository;
import com.ulima.incidenciaurbana.service.IUbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UbicacionServiceImpl implements IUbicacionService {
    
    private final UbicacionRepository ubicacionRepository;
    
    @Autowired
    public UbicacionServiceImpl(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UbicacionDTO obtenerUbicacionPorId(Long id) {
        if (id == null) {
            throw new RuntimeException("El ID de la ubicación es obligatorio");
        }
        long ubicacionId = id;
        Ubicacion ubicacion = ubicacionRepository.findById(ubicacionId)
            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada con id: " + ubicacionId));
        return convertirADTO(ubicacion);
    }
    
    @Override
    public UbicacionDTO actualizarUbicacion(Long id, UbicacionDTO ubicacionDTO) {
        if (id == null) {
            throw new RuntimeException("El ID de la ubicación es obligatorio");
        }
        long ubicacionId = id;
        Ubicacion ubicacion = ubicacionRepository.findById(ubicacionId)
            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada con id: " + ubicacionId));
        
        ubicacion.setLatitud(ubicacionDTO.getLatitud());
        ubicacion.setLongitud(ubicacionDTO.getLongitud());
        ubicacion.setDireccion(ubicacionDTO.getDireccion());
        
        ubicacion = ubicacionRepository.save(ubicacion);
        return convertirADTO(ubicacion);
    }
    
    private UbicacionDTO convertirADTO(Ubicacion ubicacion) {
        return new UbicacionDTO(
            ubicacion.getId(),
            ubicacion.getLatitud(),
            ubicacion.getLongitud(),
            ubicacion.getDireccion(),
            ubicacion.getFechaRegistro()
        );
    }
}
