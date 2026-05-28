package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rechazo_mensajes")
public class RechazoMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reporte_id", nullable = false, unique = true)
    private Reporte reporte;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    public RechazoMensaje() {
    }

    public RechazoMensaje(Reporte reporte, String descripcion) {
        this.reporte = reporte;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
