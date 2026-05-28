package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.dto.CompletarReporteRequest;
import com.ulima.incidenciaurbana.dto.TecnicoDTO;
import org.springframework.data.domain.Page;

public interface ITecnicoService {

    /**
     * Lista todos los técnicos disponibles con paginación
     * 
     * @param page Número de página
     * @return Page de TecnicoDTO
     */
    Page<TecnicoDTO> obtenerTodosTecnicos(int page);

    /**
     * Lista reportes asignados al técnico
     * 
     * @param tecnicoId ID del técnico
     * @param estado    Estado para filtrar (PROCESO, RESUELTA) - opcional
     * @param page      Número de página
     * @return Reportes asignados al técnico
     */
    Page<ReporteDTO> obtenerReportesAsignados(Long tecnicoId, String estado, int page);

    /**
     * PASO 4: Técnico escribe comentario de resolución (OBLIGATORIO)
     * 
     * Actualiza el comentario de resolución en Reporte y Asignación
     * 
     * VALIDACIONES OBLIGATORIAS:
     * ✓ Reporte existe
     * ✓ Estado es PROCESO
     * ✓ Técnico es el asignado
     * ✓ Comentario NO PUEDE ser vacío
     * ✓ Mínimo 10 caracteres
     * ✓ Max 1000 caracteres
     * 
     * @param tecnicoId            ID del técnico
     * @param reporteId            ID del reporte
     * @param comentarioResolucion Comentario del técnico sobre la resolución
     * @return ReporteDTO actualizado
     * @throws IllegalArgumentException si las validaciones fallan
     */
    // DEPRECATED: Reemplazado por completarReporte()
    // ReporteDTO actualizarComentarioResolucion(Long tecnicoId, Long reporteId,
    // String comentarioResolucion);

    /**
     * PASO 5: Técnico marca como "Completado" (VALIDACIÓN FINAL)
     * 
     * Cambia el estado del reporte a RESUELTA
     * 
     * VALIDACIONES CRÍTICAS:
     * ✓ Reporte existe
     * ✓ Estado ACTUAL es PROCESO
     * ✓ TIENE MÍNIMO 1 FOTO (del PASO 3) ← OBLIGATORIO
     * ✓ TIENE COMENTARIO (del PASO 4) ← OBLIGATORIO
     * ✓ Técnico es el asignado
     * 
     * @param tecnicoId ID del técnico
     * @param reporteId ID del reporte
     * @return ReporteDTO actualizado con estado RESUELTA
     * @throws IllegalArgumentException si las validaciones fallan
     */
    // DEPRECATED: Reemplazado por completarReporte()
    // ReporteDTO marcarResuelta(Long tecnicoId, Long reporteId);

    /**
     * ENDPOINT UNIFICADO - FLUJO OPTIMIZADO
     * 
     * Completa un reporte en estado PROCESO con fotos y comentario en UNA ÚNICA
     * llamada
     * 
     * Este método reemplaza la secuencia de 3 operaciones:
     * 1. Adjuntar fotos (1-3)
     * 2. Escribir comentario
     * 3. Marcar como RESUELTA
     * 
     * VALIDACIONES:
     * ✓ Reporte existe y está en PROCESO o RECHAZADO_AUDITO
     * ✓ Técnico es el asignado
     * ✓ Fotos: entre 1-3 válidas
     * ✓ Comentario: 10-1000 caracteres
     * 
     * TRANSACCIÓN ATÓMICA: Todo se guarda en una sola transacción (todo o nada)
     * 
     * @param tecnicoId ID del técnico
     * @param reporteId ID del reporte
     * @param request   Contiene fotos (base64) y comentario
     * @return ReporteDTO actualizado con estado RESUELTA y fotos adjuntadas
     * @throws IllegalArgumentException si las validaciones fallan
     */
    ReporteDTO completarReporte(Long tecnicoId, Long reporteId, CompletarReporteRequest request);
}
