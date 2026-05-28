package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una foto de evidencia adjunta a un reporte
 * Las fotos son OBLIGATORIAS para que un técnico pueda marcar un reporte como
 * RESUELTA
 */
@Entity
@Table(name = "fotos")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Column(nullable = false, length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoFoto tipo;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;

    // Constructores
    public Foto() {
        this.fechaCarga = LocalDateTime.now();
    }

    public Foto(Reporte reporte, String url, TipoFoto tipo) {
        this.reporte = reporte;
        this.url = url;
        this.tipo = tipo;
        this.fechaCarga = LocalDateTime.now();
    }

    public Foto(Reporte reporte, String url, TipoFoto tipo, String descripcion) {
        this(reporte, url, tipo);
        this.descripcion = descripcion;
    }

    // Getters y Setters
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

    @Override
    public String toString() {
        return "Foto{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", url='" + url + '\'' +
                ", fechaCarga=" + fechaCarga +
                '}';
    }
}
