package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.CiudadanoDTO;

public interface ICiudadanoService {
    CiudadanoDTO obtenerCiudadanoPorId(Long id);
    CiudadanoDTO actualizarCiudadano(Long id, CiudadanoDTO ciudadanoDTO);
    void eliminarCiudadano(Long id);
}
