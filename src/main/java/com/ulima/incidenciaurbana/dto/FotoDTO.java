package com.ulima.incidenciaurbana.dto;

import com.ulima.incidenciaurbana.model.TipoFoto;
import java.time.LocalDateTime;

/**
 * DTO para representar una foto de evidencia
 */
public class FotoDTO {

    private Long id;
    private Long reporteId;
    private String url;
    private TipoFoto tipo;
    private String descripcion;
    private LocalDateTime fechaCarga;
    private String archivoBase64; // Base64 de la imagen para mostrar en apps móviles

    // Constructores
    public FotoDTO() {
    }

    public FotoDTO(Long id, Long reporteId, String url, TipoFoto tipo, LocalDateTime fechaCarga) {
        this.id = id;
        this.reporteId = reporteId;
        this.url = url;
        this.tipo = tipo;
        this.fechaCarga = fechaCarga;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporteId() {
        return reporteId;
    }

    public void setReporteId(Long reporteId) {
        this.reporteId = reporteId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TipoFoto getTipo() {
        return tipo;
    }

    public void setTipo(TipoFoto tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public String getArchivoBase64() {
        return archivoBase64;
    }

    public void setArchivoBase64(String archivoBase64) {
        this.archivoBase64 = archivoBase64;
    }
}
