package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.dto.CompletarReporteRequest;
import com.ulima.incidenciaurbana.dto.TecnicoDTO;
import com.ulima.incidenciaurbana.service.ITecnicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tecnicos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TecnicoController {

    private final ITecnicoService tecnicoService;

    @Autowired
    public TecnicoController(ITecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    /**
     * GET /api/tecnicos?page=0
     * Lista todos los técnicos disponibles con paginación
     */
    @GetMapping
    public ResponseEntity<Page<TecnicoDTO>> obtenerTodosTecnicos(
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<TecnicoDTO> tecnicos = tecnicoService.obtenerTodosTecnicos(page);
        return ResponseEntity.ok(tecnicos);
    }

    /**
     * GET /api/tecnicos/{id}/reportes?estado=PROCESO&page=0
     * Lista reportes asignados filtrados por estado
     */
    @GetMapping("/{id}/reportes")
    public ResponseEntity<Page<ReporteDTO>> obtenerReportesAsignados(
            @PathVariable Long id,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        Page<ReporteDTO> reportes = tecnicoService.obtenerReportesAsignados(id, estado, page);
        return ResponseEntity.ok(reportes);
    }

    /**
     * PATCH /api/tecnicos/{id}/reportes/{reporteId}/completar
     * Completa un reporte con fotos y comentario
     */
    @PatchMapping("/{id}/reportes/{reporteId}/completar")
    public ResponseEntity<?> completarReporte(
            @PathVariable Long id,
            @PathVariable Long reporteId,
            @Valid @RequestBody CompletarReporteRequest request) {

        try {
            ReporteDTO reporteDTO = tecnicoService.completarReporte(id, reporteId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reporte completado exitosamente y marcado como RESUELTA");
            response.put("reporte", reporteDTO);
            response.put("fotosAdjuntadas", request.getFotos().size());
            response.put("estadoFinal", "RESUELTA");
            response.put("proximoPaso",
                    "El operador municipal puede revisar el reporte en estado RESUELTA para auditoría");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Validación fallida");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al completar reporte");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
