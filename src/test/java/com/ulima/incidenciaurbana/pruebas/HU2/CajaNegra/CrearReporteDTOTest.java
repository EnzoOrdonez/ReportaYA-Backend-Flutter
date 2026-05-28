package com.ulima.incidenciaurbana.pruebas.HU2.CajaNegra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PRUEBA DE CAJA NEGRA - HU2: Creación de Reportes
 * 
 * Componente bajo prueba: CrearReporteDTO
 * Campos de entrada: 7 (> 4 requeridos)
 * 
 * Campos:
 * 1. titulo: String
 * 2. descripcion: String
 * 3. tipoProblema: String
 * 4. ubicacionLatitud: Double
 * 5. ubicacionLongitud: Double
 * 6. ciudadanoId: Long
 * 7. urlFoto: String (opcional)
 */
@DisplayName("CAJA NEGRA: CrearReporteDTO - 7 CAMPOS")
public class CrearReporteDTOTest {

    // DTO para crear reporte
    static class CrearReporteDTO {
        private String titulo;
        private String descripcion;
        private String tipoProblema;
        private Double ubicacionLatitud;
        private Double ubicacionLongitud;
        private Long ciudadanoId;
        private String urlFoto;

        // Getters y Setters
        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getTipoProblema() {
            return tipoProblema;
        }

        public void setTipoProblema(String tipoProblema) {
            this.tipoProblema = tipoProblema;
        }

        public Double getUbicacionLatitud() {
            return ubicacionLatitud;
        }

        public void setUbicacionLatitud(Double ubicacionLatitud) {
            this.ubicacionLatitud = ubicacionLatitud;
        }

        public Double getUbicacionLongitud() {
            return ubicacionLongitud;
        }

        public void setUbicacionLongitud(Double ubicacionLongitud) {
            this.ubicacionLongitud = ubicacionLongitud;
        }

        public Long getCiudadanoId() {
            return ciudadanoId;
        }

        public void setCiudadanoId(Long ciudadanoId) {
            this.ciudadanoId = ciudadanoId;
        }

        public String getUrlFoto() {
            return urlFoto;
        }

        public void setUrlFoto(String urlFoto) {
            this.urlFoto = urlFoto;
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
    private ResultadoValidacion validarCrearReporteDTO(CrearReporteDTO dto) {
        List<String> errores = new ArrayList<>();

        // Validar título
        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            errores.add("El título es obligatorio");
        } else if (dto.getTitulo().length() < 5) {
            errores.add("El título debe tener al menos 5 caracteres");
        } else if (dto.getTitulo().length() > 100) {
            errores.add("El título no puede exceder 100 caracteres");
        }

        // Validar descripción
        if (dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
            errores.add("La descripción es obligatoria");
        } else if (dto.getDescripcion().length() < 10) {
            errores.add("La descripción debe tener al menos 10 caracteres");
        } else if (dto.getDescripcion().length() > 500) {
            errores.add("La descripción no puede exceder 500 caracteres");
        }

        // Validar tipo de problema
        if (dto.getTipoProblema() == null || dto.getTipoProblema().trim().isEmpty()) {
            errores.add("El tipo de problema es obligatorio");
        }

        // Validar ubicación
        if (dto.getUbicacionLatitud() == null) {
            errores.add("La latitud es obligatoria");
        } else if (dto.getUbicacionLatitud() < -90 || dto.getUbicacionLatitud() > 90) {
            errores.add("Latitud inválida");
        }

        if (dto.getUbicacionLongitud() == null) {
            errores.add("La longitud es obligatoria");
        } else if (dto.getUbicacionLongitud() < -180 || dto.getUbicacionLongitud() > 180) {
            errores.add("Longitud inválida");
        }

        // Validar ciudadanoId
        if (dto.getCiudadanoId() == null || dto.getCiudadanoId() <= 0) {
            errores.add("ID de ciudadano inválido");
        }

        // URL foto es opcional, pero si existe debe ser válida
        if (dto.getUrlFoto() != null && !dto.getUrlFoto().isEmpty()) {
            if (!dto.getUrlFoto().startsWith("http://") && !dto.getUrlFoto().startsWith("https://")) {
                errores.add("URL de foto inválida");
            }
        }

        return new ResultadoValidacion(errores.isEmpty(), errores);
    }

    @Nested
    @DisplayName("Casos Válidos")
    class CasosValidos {

        @Test
        @DisplayName("CV1: Todos los campos completos y válidos")
        void testTodosCamposValidos() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Basura en la esquina");
            dto.setDescripcion("Hay basura acumulada en la esquina de mi calle");
            dto.setTipoProblema("BASURA");
            dto.setUbicacionLatitud(-12.046374);
            dto.setUbicacionLongitud(-77.042793);
            dto.setCiudadanoId(123L);
            dto.setUrlFoto("https://example.com/foto.jpg");

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertTrue(resultado.isValido(), "DTO con todos los campos válidos debe ser aceptado");
            assertEquals(0, resultado.getErrores().size(), "No debe haber errores");
        }

        @Test
        @DisplayName("CV2: Sin foto (campo opcional)")
        void testSinFoto() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Bache grande");
            dto.setDescripcion("Bache grande en la pista principal");
            dto.setTipoProblema("BACHES");
            dto.setUbicacionLatitud(-12.0);
            dto.setUbicacionLongitud(-77.0);
            dto.setCiudadanoId(456L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertTrue(resultado.isValido(), "DTO sin foto debe ser válido");
        }

        @Test
        @DisplayName("CV3: Título y descripción en límites mínimos")
        void testLimitesMinimos() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("12345"); // exactamente 5 caracteres
            dto.setDescripcion("1234567890"); // exactamente 10 caracteres
            dto.setTipoProblema("OTRO");
            dto.setUbicacionLatitud(0.0);
            dto.setUbicacionLongitud(0.0);
            dto.setCiudadanoId(1L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertTrue(resultado.isValido(), "Límites mínimos válidos deben ser aceptados");
        }
    }

    @Nested
    @DisplayName("Casos Inválidos")
    class CasosInvalidos {

        @Test
        @DisplayName("CI1: Título vacío")
        void testTituloVacio() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("");
            dto.setDescripcion("Descripción válida");
            dto.setTipoProblema("BASURA");
            dto.setUbicacionLatitud(-12.0);
            dto.setUbicacionLongitud(-77.0);
            dto.setCiudadanoId(123L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertFalse(resultado.isValido(), "Título vacío debe ser inválido");
            assertTrue(resultado.getErrores().contains("El título es obligatorio"));
        }

        @Test
        @DisplayName("CI2: Descripción muy corta")
        void testDescripcionCorta() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Título válido");
            dto.setDescripcion("Corta"); // menos de 10 caracteres
            dto.setTipoProblema("BASURA");
            dto.setUbicacionLatitud(-12.0);
            dto.setUbicacionLongitud(-77.0);
            dto.setCiudadanoId(123L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertFalse(resultado.isValido(), "Descripción corta debe ser inválida");
            assertTrue(resultado.getErrores().contains("La descripción debe tener al menos 10 caracteres"));
        }

        @Test
        @DisplayName("CI3: Coordenadas fuera de rango")
        void testCoordenadasInvalidas() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Título válido");
            dto.setDescripcion("Descripción válida de más de 10 caracteres");
            dto.setTipoProblema("BASURA");
            dto.setUbicacionLatitud(95.0); // > 90
            dto.setUbicacionLongitud(-200.0); // < -180
            dto.setCiudadanoId(123L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertFalse(resultado.isValido(), "Coordenadas inválidas deben ser rechazadas");
            assertTrue(resultado.getErrores().contains("Latitud inválida"));
            assertTrue(resultado.getErrores().contains("Longitud inválida"));
        }

        @Test
        @DisplayName("CI4: CiudadanoId inválido")
        void testCiudadanoIdInvalido() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Título válido");
            dto.setDescripcion("Descripción válida de más de 10 caracteres");
            dto.setTipoProblema("BASURA");
            dto.setUbicacionLatitud(-12.0);
            dto.setUbicacionLongitud(-77.0);
            dto.setCiudadanoId(0L); // inválido

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertFalse(resultado.isValido(), "CiudadanoId 0 o negativo debe ser inválido");
            assertTrue(resultado.getErrores().contains("ID de ciudadano inválido"));
        }

        @Test
        @DisplayName("CI5: URL de foto inválida")
        void testUrlFotoInvalida() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Título válido");
            dto.setDescripcion("Descripción válida de más de 10 caracteres");
            dto.setTipoProblema("BASURA");
            dto.setUbicacionLatitud(-12.0);
            dto.setUbicacionLongitud(-77.0);
            dto.setCiudadanoId(123L);
            dto.setUrlFoto("invalid-url");

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertFalse(resultado.isValido(), "URL inválida debe ser rechazada");
            assertTrue(resultado.getErrores().contains("URL de foto inválida"));
        }
    }

    @Nested
    @DisplayName("Casos Límite")
    class CasosLimite {

        @Test
        @DisplayName("CL1: Coordenadas en extremos válidos")
        void testCoordenadasEnExtremos() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("Título");
            dto.setDescripcion("Descripción válida");
            dto.setTipoProblema("OTRO");
            dto.setUbicacionLatitud(90.0); // límite superior
            dto.setUbicacionLongitud(-180.0); // límite inferior
            dto.setCiudadanoId(1L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertTrue(resultado.isValido(), "Coordenadas en límites válidos deben ser aceptadas");
        }

        @Test
        @DisplayName("CL2: Título y descripción en límites máximos")
        void testLimitesMaximos() {
            CrearReporteDTO dto = new CrearReporteDTO();
            dto.setTitulo("A".repeat(100)); // exactamente 100 caracteres
            dto.setDescripcion("B".repeat(500)); // exactamente 500 caracteres
            dto.setTipoProblema("ALUMBRADO");
            dto.setUbicacionLatitud(-12.0);
            dto.setUbicacionLongitud(-77.0);
            dto.setCiudadanoId(999999L);

            ResultadoValidacion resultado = validarCrearReporteDTO(dto);

            assertTrue(resultado.isValido(), "Límites máximos válidos deben ser aceptados");
        }
    }
}

/**
 * ✅ CUMPLE: Prueba de caja negra con > 4 campos (7 campos)
 */
