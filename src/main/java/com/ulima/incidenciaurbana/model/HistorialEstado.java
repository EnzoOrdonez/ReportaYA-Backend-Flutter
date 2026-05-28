package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoReporte estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoReporte estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    public HistorialEstado() {
    }

    public HistorialEstado(Reporte reporte, EstadoReporte estadoAnterior, EstadoReporte estadoNuevo) {
        this.reporte = reporte;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
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
