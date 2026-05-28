package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mensaje_notificaciones")
public class MensajeNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, unique = true)
    private EstadoReporte estado;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    public MensajeNotificacion() {
    }

    public MensajeNotificacion(EstadoReporte estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
