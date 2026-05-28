package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.HistorialEstadoDTO;
import java.util.List;

public interface IHistorialEstadoService {
    List<HistorialEstadoDTO> obtenerHistorialPorReporte(Long reporteId);
}
