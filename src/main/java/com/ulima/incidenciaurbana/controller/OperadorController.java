package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.CerrarReporteRequest;
import com.ulima.incidenciaurbana.dto.RechazarAuditoriaRequest;
import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.service.IOperadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operador")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OperadorController {

    private final IOperadorService operadorService;

    @Autowired
    public OperadorController(IOperadorService operadorService) {
        this.operadorService = operadorService;
    }

    /**
     * GET /api/operador/reportes-auditoria?estado=RESUELTA&page=0
     * Obtiene reportes filtrados por estado para auditoría
     *
     * @param estado el estado del reporte (p.ej., RESUELTA)
     * @param page   número de página (0-based)
     * @return Page de ReporteDTO
     */
    @GetMapping("/reportes-auditoria")
    public ResponseEntity<Page<ReporteDTO>> obtenerReportesParaAuditoria(
            @RequestParam(name = "estado", defaultValue = "RESUELTA") String estado,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            Page<ReporteDTO> reportes = operadorService.obtenerReportesParaAuditoria(estado, page);
            return ResponseEntity.ok(reportes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/operador/reportes/{id}/cerrar
     * Cierra un reporte aprobando la auditoría
     *
     * @param id      el ID del reporte
     * @param request contiene operadorId y comentarioCierre
     * @return ReporteDTO actualizado
     */
    @PostMapping("/reportes/{id}/cerrar")
    public ResponseEntity<?> cerrarReporte(
            @PathVariable Long id,
            @RequestBody CerrarReporteRequest request) {
        try {
            ReporteDTO reporteDTO = operadorService.cerrarReporte(id, request.getOperadorId(),
                    request.getComentarioCierre());
            return ResponseEntity.ok(reporteDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * POST /api/operador/reportes/{id}/rechazar-audito
     * Rechaza un reporte (auto-cierra si contador >= 3)
     */
    @PostMapping("/reportes/{id}/rechazar-audito")
    public ResponseEntity<?> rechazarAudito(
            @PathVariable Long id,
            @RequestBody RechazarAuditoriaRequest request) {
        try {
            ReporteDTO reporteDTO = operadorService.rechazarAudito(id, request.getOperadorId(),
                    request.getComentarioRechazo());
            return ResponseEntity.ok(reporteDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
