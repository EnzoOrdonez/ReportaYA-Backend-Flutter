package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.HistorialEstadoDTO;
import com.ulima.incidenciaurbana.model.HistorialEstado;
import com.ulima.incidenciaurbana.repository.HistorialEstadoRepository;
import com.ulima.incidenciaurbana.service.IHistorialEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HistorialEstadoServiceImpl implements IHistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    @Autowired
    public HistorialEstadoServiceImpl(HistorialEstadoRepository historialEstadoRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoDTO> obtenerHistorialPorReporte(Long reporteId) {
        if (reporteId == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        return historialEstadoRepository.findByReporteIdOrderByFechaCambioDesc(reporteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private HistorialEstadoDTO convertirADTO(HistorialEstado historial) {
        return new HistorialEstadoDTO(
                historial.getId(),
                historial.getReporte().getId(),
                historial.getEstadoAnterior(),
                historial.getEstadoNuevo(),
                historial.getFechaCambio()
        );
    }
}
