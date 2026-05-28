package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.Prioridad;
import com.ulima.incidenciaurbana.service.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final IReporteService reporteService;

    @Autowired
    public ReporteController(IReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping
    public ResponseEntity<ReporteDTO> crearReporte(@RequestBody ReporteDTO reporteDTO) {
        try {
            ReporteDTO nuevoReporte = reporteService.crearReporte(reporteDTO);
            return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<ReporteDTO>> obtenerTodosReportes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "estado", required = false) EstadoReporte estado) {
        var reportes = reporteService.obtenerTodosReportes(page, estado);
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }

    @GetMapping("/mapa")
    public ResponseEntity<java.util.List<ReporteDTO>> obtenerReportesMapa(
            @RequestParam(name = "estado", required = false) EstadoReporte estado,
            @RequestParam(name = "tipo", required = false) com.ulima.incidenciaurbana.model.TipoProblema tipo,
            @RequestParam(name = "prioridad", required = false) Prioridad prioridad) {
        var reportes = reporteService.obtenerReportesMapa(estado, tipo, prioridad);
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<org.springframework.data.domain.Page<ReporteDTO>> obtenerReportesPorCuenta(
            @PathVariable Long cuentaId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        var reportes = reporteService.obtenerReportesPorCuenta(cuentaId, page);
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDTO> obtenerReportePorId(@PathVariable Long id) {
        try {
            ReporteDTO reporte = reporteService.obtenerReportePorId(id);
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Operador-specific endpoint removed. Use GET /api/reportes?page={n} instead.

    @PutMapping("/{id}")
    public ResponseEntity<ReporteDTO> actualizarReporte(
            @PathVariable Long id,
            @RequestBody ReporteDTO reporteDTO) {
        try {
            ReporteDTO reporteActualizado = reporteService.actualizarReporte(id, reporteDTO);
            return new ResponseEntity<>(reporteActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReporteDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoReporte nuevoEstado) {
        try {
            ReporteDTO reporteActualizado = reporteService.cambiarEstadoReporte(id, nuevoEstado);
            return new ResponseEntity<>(reporteActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<ReporteDTO> rechazarReporte(
            @PathVariable Long id,
            @RequestParam String motivo) {
        try {
            ReporteDTO reporteActualizado = reporteService.rechazarReporte(id, motivo);
            return new ResponseEntity<>(reporteActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/prioridad")
    public ResponseEntity<ReporteDTO> cambiarPrioridad(
            @PathVariable Long id,
            @RequestParam Prioridad nuevaPrioridad) {
        try {
            ReporteDTO reporteActualizado = reporteService.cambiarPrioridadReporte(id, nuevaPrioridad);
            return new ResponseEntity<>(reporteActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
