package com.ulima.incidenciaurbana.dto;

import com.ulima.incidenciaurbana.model.EstadoReporte;
import java.time.LocalDateTime;

public class HistorialEstadoDTO {
    private Long id;
    private Long reporteId;
    private EstadoReporte estadoAnterior;
    private EstadoReporte estadoNuevo;
    private LocalDateTime fechaCambio;

    public HistorialEstadoDTO() {
    }

    public HistorialEstadoDTO(Long id, Long reporteId, EstadoReporte estadoAnterior, EstadoReporte estadoNuevo, LocalDateTime fechaCambio) {
        this.id = id;
        this.reporteId = reporteId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = fechaCambio;
    }

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

    public EstadoReporte getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoReporte estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoReporte getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoReporte estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}
