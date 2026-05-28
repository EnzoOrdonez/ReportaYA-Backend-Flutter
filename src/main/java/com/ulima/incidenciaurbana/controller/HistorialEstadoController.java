package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.HistorialEstadoDTO;
import com.ulima.incidenciaurbana.service.IHistorialEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial-estados")
public class HistorialEstadoController {

    private final IHistorialEstadoService historialEstadoService;

    @Autowired
    public HistorialEstadoController(IHistorialEstadoService historialEstadoService) {
        this.historialEstadoService = historialEstadoService;
    }

    @GetMapping("/reporte/{reporteId}")
    public ResponseEntity<List<HistorialEstadoDTO>> obtenerHistorialPorReporte(@PathVariable Long reporteId) {
        List<HistorialEstadoDTO> historial = historialEstadoService.obtenerHistorialPorReporte(reporteId);
        return ResponseEntity.ok(historial);
    }
}
