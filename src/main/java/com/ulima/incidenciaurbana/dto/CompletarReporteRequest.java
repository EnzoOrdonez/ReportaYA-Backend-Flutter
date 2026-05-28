package com.ulima.incidenciaurbana.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO para completar un reporte con fotos y comentario en una sola solicitud
 * PATCH /api/tecnicos/{tecnicoId}/reportes/{reporteId}/completar
 */
public class CompletarReporteRequest {

    @NotBlank(message = "El comentario de resolución es obligatorio")
    @Size(min = 10, max = 1000, message = "El comentario debe tener entre 10 y 1000 caracteres")
    private String comentarioResolucion;

    @NotNull(message = "Las fotos son obligatorias")
    @Size(min = 1, max = 3, message = "Debe adjuntar entre 1 y 3 fotos")
    private List<FotoRequestInline> fotos;

    public CompletarReporteRequest() {
    }

    public CompletarReporteRequest(String comentarioResolucion, List<FotoRequestInline> fotos) {
        this.comentarioResolucion = comentarioResolucion;
        this.fotos = fotos;
    }

    public String getComentarioResolucion() {
        return comentarioResolucion;
    }

    public void setComentarioResolucion(String comentarioResolucion) {
        this.comentarioResolucion = comentarioResolucion;
    }

    public List<FotoRequestInline> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoRequestInline> fotos) {
        this.fotos = fotos;
    }

    @Override
    public String toString() {
        return "CompletarReporteRequest{" +
                "comentarioResolucion='" + comentarioResolucion + '\'' +
                ", fotosCount=" + (fotos != null ? fotos.size() : 0) +
                '}';
    }

    /**
     * Clase interna para representar fotos con datos base64
     */
    public static class FotoRequestInline {

        @NotBlank(message = "La foto en base64 es obligatoria")
        private String archivoBase64;

        @NotBlank(message = "El tipo de foto es obligatorio (INICIAL, PROCESO, FINAL)")
        private String tipo;

        @NotBlank(message = "La descripción de la foto es obligatoria")
        @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
        private String descripcion;

        public FotoRequestInline() {
        }

        public FotoRequestInline(String archivoBase64, String tipo, String descripcion) {
            this.archivoBase64 = archivoBase64;
            this.tipo = tipo;
            this.descripcion = descripcion;
        }

        public String getArchivoBase64() {
            return archivoBase64;
        }

        public void setArchivoBase64(String archivoBase64) {
            this.archivoBase64 = archivoBase64;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return "FotoRequestInline{" +
                    "tipo='" + tipo + '\'' +
                    ", descripcion='" + descripcion + '\'' +
                    ", archivoBase64Length=" + (archivoBase64 != null ? archivoBase64.length() : 0) +
                    '}';
        }
    }
}
