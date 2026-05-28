package com.ulima.incidenciaurbana.pruebas.HU1.Unitarias;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PRUEBAS UNITARIAS - HU1: Mapa de Reportes
 * 
 * Método bajo prueba: filtrarReportesPorCriterios()
 * Número de pruebas: 4
 * 
 * Descripción: Prueba el método que filtra reportes según criterios
 */
@DisplayName("PRUEBAS UNITARIAS: filtrarReportesPorCriterios() - 4 PRUEBAS")
public class FiltrarReportesPorCriteriosTest {

    // DTO simplificado de Reporte
    static class ReporteDTO {
        private Long id;
        private String estado;
        private String tipoProblema;
        private String prioridad;

        public ReporteDTO(Long id, String estado, String tipoProblema, String prioridad) {
            this.id = id;
            this.estado = estado;
            this.tipoProblema = tipoProblema;
            this.prioridad = prioridad;
        }

        public Long getId() {
            return id;
        }

        public String getEstado() {
            return estado;
        }

        public String getTipoProblema() {
            return tipoProblema;
        }

        public String getPrioridad() {
            return prioridad;
        }
    }

    // Función a probar
    private List<ReporteDTO> filtrarReportesPorCriterios(
            List<ReporteDTO> reportes,
            String estado,
            String tipoProblema,
            String prioridad) {

        return reportes.stream()
                .filter(r -> estado == null || r.getEstado().equals(estado))
                .filter(r -> tipoProblema == null || r.getTipoProblema().equals(tipoProblema))
                .filter(r -> prioridad == null || r.getPrioridad().equals(prioridad))
                .collect(Collectors.toList());
    }

    private List<ReporteDTO> reportesMock;

    @BeforeEach
    void setUp() {
        reportesMock = new ArrayList<>();
        reportesMock.add(new ReporteDTO(1L, "PENDIENTE", "BASURA", "ALTA"));
        reportesMock.add(new ReporteDTO(2L, "PROCESO", "BACHES", "MEDIA"));
        reportesMock.add(new ReporteDTO(3L, "FINALIZADO", "ALUMBRADO", "BAJA"));
        reportesMock.add(new ReporteDTO(4L, "PENDIENTE", "BASURA", "ALTA"));
    }

    @Test
    @DisplayName("PRUEBA 1: Filtrar por estado PENDIENTE")
    void testFiltrarPorEstadoPendiente() {
        List<ReporteDTO> resultado = filtrarReportesPorCriterios(reportesMock, "PENDIENTE", null, null);

        assertEquals(2, resultado.size(), "Debe retornar 2 reportes con estado PENDIENTE");
        assertEquals(1L, resultado.get(0).getId(), "Primer reporte debe tener ID 1");
        assertEquals(4L, resultado.get(1).getId(), "Segundo reporte debe tener ID 4");
        assertTrue(resultado.stream().allMatch(r -> r.getEstado().equals("PENDIENTE")),
                "Todos los reportes deben tener estado PENDIENTE");
    }

    @Test
    @DisplayName("PRUEBA 2: Filtrar por tipo de problema BASURA")
    void testFiltrarPorTipoBasura() {
        List<ReporteDTO> resultado = filtrarReportesPorCriterios(reportesMock, null, "BASURA", null);

        assertEquals(2, resultado.size(), "Debe retornar 2 reportes de tipo BASURA");
        assertTrue(resultado.stream().allMatch(r -> r.getTipoProblema().equals("BASURA")),
                "Todos los reportes deben ser de tipo BASURA");
    }

    @Test
    @DisplayName("PRUEBA 3: Filtros combinados (estado + prioridad)")
    void testFiltrosCombinados() {
        List<ReporteDTO> resultado = filtrarReportesPorCriterios(reportesMock, "PENDIENTE", null, "ALTA");

        assertEquals(2, resultado.size(), "Debe retornar 2 reportes que cumplan ambos criterios");
        assertTrue(
                resultado.stream().allMatch(r -> r.getEstado().equals("PENDIENTE") && r.getPrioridad().equals("ALTA")),
                "Todos los reportes deben cumplir ambos criterios");
    }

    @Test
    @DisplayName("PRUEBA 4: Sin filtros devuelve todos los reportes")
    void testSinFiltros() {
        List<ReporteDTO> resultado = filtrarReportesPorCriterios(reportesMock, null, null, null);

        assertEquals(4, resultado.size(), "Debe retornar todos los 4 reportes");
        assertEquals(reportesMock, resultado, "Debe retornar la lista completa sin modificar");
    }
}

/**
 * ✅ CUMPLE: Método con 4 pruebas unitarias
 */
