package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.ReporteDTO;
import org.springframework.data.domain.Page;

/**
 * Servicio para operaciones de auditoría del Operador Municipal
 * Separación de responsabilidades: auditoría de reportes
 */
public interface IOperadorService {

    /**
     * Obtiene reportes filtrados por estado para auditoría
     * 
     * @param estado Estado del reporte
     * @param page   Número de página
     * @return Página de reportes
     */
    Page<ReporteDTO> obtenerReportesParaAuditoria(String estado, int page);

    /**
     * Operador aprueba auditoría - cambia estado a CERRADA
     * 
     * @param reporteId        ID del reporte
     * @param operadorId       ID del operador municipal
     * @param comentarioCierre Comentario del cierre (opcional)
     * @return Reporte actualizado
     */
    ReporteDTO cerrarReporte(Long reporteId, Long operadorId, String comentarioCierre);

    /**
     * Operador rechaza auditoría - con lógica inteligente de cierre automático
     * 
     * Comportamiento:
     * - Si contador < 3: RESUELTA → RECHAZADO_AUDITO (permite reintentos)
     * - Si contador >= 3: RESUELTA → RECHAZADO (cierre definitivo automático)
     * 
     * @param reporteId         ID del reporte
     * @param operadorId        ID del operador municipal
     * @param comentarioRechazo Comentario OBLIGATORIO del rechazo
     * @return Reporte actualizado
     */
    ReporteDTO rechazarAudito(Long reporteId, Long operadorId, String comentarioRechazo);
}
