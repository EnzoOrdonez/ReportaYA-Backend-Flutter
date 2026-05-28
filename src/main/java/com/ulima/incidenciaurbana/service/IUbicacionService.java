package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.UbicacionDTO;

public interface IUbicacionService {
    
    /**
     * Obtiene una ubicación por su ID
     * @param id ID de la ubicación
     * @return UbicacionDTO con los datos de la ubicación
     */
    UbicacionDTO obtenerUbicacionPorId(Long id);
    
    /**
     * Actualiza una ubicación existente
     * @param id ID de la ubicación a actualizar
     * @param ubicacionDTO Datos actualizados de la ubicación
     * @return UbicacionDTO con los datos actualizados
     */
    UbicacionDTO actualizarUbicacion(Long id, UbicacionDTO ubicacionDTO);
}
