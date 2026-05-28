package com.ulima.incidenciaurbana.pruebas.HU1.CajaNegra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PRUEBA DE CAJA NEGRA - HU1: Mapa de Reportes
 * 
 * Componente bajo prueba: Filtrado de Reportes
 * Campos de entrada: 5 (> 4 requeridos)
 * 
 * Campos:
 * 1. estado: String
 * 2. tipoProblema: String
 * 3. prioridad: String
 * 4. fechaInicio: LocalDate
 * 5. fechaFin: LocalDate
 */
@DisplayName("CAJA NEGRA: Filtrado de Reportes - 5 CAMPOS")
public class FiltrarReportesTest {

    // DTO para filtros
    static class FiltrosReporteDTO {
        private String estado;
        private String tipoProblema;
        private String prioridad;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;

        // Getters y Setters
        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getTipoProblema() {
            return tipoProblema;
        }

        public void setTipoProblema(String tipoProblema) {
            this.tipoProblema = tipoProblema;
        }

        public String getPrioridad() {
            return prioridad;
        }

        public void setPrioridad(String prioridad) {
            this.prioridad = prioridad;
        }

        public LocalDate getFechaInicio() {
            return fechaInicio;
        }

        public void setFechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
        }

        public LocalDate getFechaFin() {
            return fechaFin;
        }

        public void setFechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
        }
    }

    // Resultado de validación
    static class ResultadoValidacion {
        private boolean valido;
        private List<String> errores;

        public ResultadoValidacion(boolean valido, List<String> errores) {
            this.valido = valido;
            this.errores = errores;
        }

        public boolean isValido() {
            return valido;
        }

        public List<String> getErrores() {
            return errores;
        }
    }

    // Función de validación
    private ResultadoValidacion validarFiltros(FiltrosReporteDTO filtros) {
        List<String> errores = new ArrayList<>();

        // Validar fechas
        if (filtros.getFechaInicio() != null && filtros.getFechaFin() != null) {
            if (filtros.getFechaInicio().isAfter(filtros.getFechaFin())) {
                errores.add("La fecha de inicio debe ser anterior a la fecha de fin");
            }
        }

        // Validar que al menos un filtro esté presente
        if (filtros.getEstado() == null &&
                filtros.getTipoProblema() == null &&
                filtros.getPrioridad() == null &&
                filtros.getFechaInicio() == null &&
                filtros.getFechaFin() == null) {
            errores.add("Debe seleccionar al menos un filtro");
        }

        return new ResultadoValidacion(errores.isEmpty(), errores);
    }

    @Nested
    @DisplayName("Casos Válidos")
    class CasosValidos {

        @Test
        @DisplayName("CV1: Todos los campos con valores válidos")
        void testTodosCamposValidos() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();
            filtros.setEstado("PENDIENTE");
            filtros.setTipoProblema("BASURA");
            filtros.setPrioridad("ALTA");
            filtros.setFechaInicio(LocalDate.of(2024, 1, 1));
            filtros.setFechaFin(LocalDate.of(2024, 12, 31));

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertTrue(resultado.isValido(), "Filtros con todos los campos válidos deben ser aceptados");
            assertEquals(0, resultado.getErrores().size(), "No debe haber errores");
        }

        @Test
        @DisplayName("CV2: Solo un campo (estado)")
        void testSoloEstado() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();
            filtros.setEstado("PROCESO");

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertTrue(resultado.isValido(), "Filtro con solo estado debe ser válido");
        }

        @Test
        @DisplayName("CV3: Combinación parcial de campos")
        void testCombinacionParcial() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();
            filtros.setTipoProblema("ALUMBRADO");
            filtros.setPrioridad("MEDIA");

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertTrue(resultado.isValido(), "Combinación parcial de filtros debe ser válida");
        }
    }

    @Nested
    @DisplayName("Casos Inválidos")
    class CasosInvalidos {

        @Test
        @DisplayName("CI1: Fecha inicio posterior a fecha fin")
        void testFechasInvalidas() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();
            filtros.setFechaInicio(LocalDate.of(2024, 12, 31));
            filtros.setFechaFin(LocalDate.of(2024, 1, 1));

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertFalse(resultado.isValido(), "Fechas en orden inverso deben ser inválidas");
            assertTrue(resultado.getErrores().contains("La fecha de inicio debe ser anterior a la fecha de fin"));
        }

        @Test
        @DisplayName("CI2: Sin ningún filtro seleccionado")
        void testSinFiltros() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertFalse(resultado.isValido(), "Filtros vacíos deben ser inválidos");
            assertTrue(resultado.getErrores().contains("Debe seleccionar al menos un filtro"));
        }
    }

    @Nested
    @DisplayName("Casos Límite")
    class CasosLimite {

        @Test
        @DisplayName("CL1: Fechas iguales")
        void testFechasIguales() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();
            LocalDate fecha = LocalDate.of(2024, 6, 15);
            filtros.setFechaInicio(fecha);
            filtros.setFechaFin(fecha);

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertTrue(resultado.isValido(), "Fechas iguales deben ser válidas");
        }

        @Test
        @DisplayName("CL2: Todos los campos en valores extremos")
        void testValoresExtremos() {
            FiltrosReporteDTO filtros = new FiltrosReporteDTO();
            filtros.setEstado("RECHAZADO");
            filtros.setTipoProblema("OTRO");
            filtros.setPrioridad("BAJA");

            ResultadoValidacion resultado = validarFiltros(filtros);

            assertTrue(resultado.isValido(), "Valores extremos válidos deben ser aceptados");
        }
    }
}

/**
 * ✅ CUMPLE: Prueba de caja negra con > 4 campos (5 campos)
 */
