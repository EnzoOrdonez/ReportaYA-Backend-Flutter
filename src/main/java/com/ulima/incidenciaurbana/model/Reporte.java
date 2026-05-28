package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_problema")
    private TipoProblema tipoProblema = TipoProblema.OTROS;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id", nullable = false)
    private Ubicacion ubicacion;

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos = new ArrayList<>();

    // Campo OBLIGATORIO para que técnico pueda marcar como RESUELTA
    @Column(name = "comentario_resolucion", length = 1000)
    private String comentarioResolucion;

    // Campos de Auditoría (ESCENARIO 2 y 3)
    @Column(name = "comentario_cierre", length = 1000)
    private String comentarioCierre;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_cierre_id")
    private Cuenta operadorCierre;

    @Column(name = "comentario_rechazo", length = 1000)
    private String comentarioRechazo;

    @Column(name = "fecha_rechazo")
    private LocalDateTime fechaRechazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_rechazo_id")
    private Cuenta operadorRechazo;

    @Column(name = "contador_rechazos", columnDefinition = "INT DEFAULT 0")
    private Integer contadorRechazos = 0;

    // Constructors
    public Reporte() {
        this.estado = EstadoReporte.PENDIENTE;
        this.prioridad = Prioridad.MEDIA;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Reporte(String titulo, String descripcion, Cuenta cuenta) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cuenta = cuenta;
    }

    // Business Methods
    public void cambiarEstado(EstadoReporte nuevoEstado) {
        this.estado = nuevoEstado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void cambiarPrioridad(Prioridad nuevaPrioridad) {
        this.prioridad = nuevaPrioridad;
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Assignment helpers
    public void agregarAsignacion(Asignacion asignacion) {
        asignaciones.add(asignacion);
        asignacion.setReporte(this);
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void cerrarAsignacionActiva() {
        for (Asignacion a : asignaciones) {
            if (a.getFechaCierre() == null) {
                a.setFechaCierre(LocalDateTime.now());
            }
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

    public TipoProblema getTipoProblema() {
        return tipoProblema;
    }

    public void setTipoProblema(TipoProblema tipoProblema) {
        this.tipoProblema = tipoProblema;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<Asignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public String getComentarioResolucion() {
        return comentarioResolucion;
    }

    public void setComentarioResolucion(String comentarioResolucion) {
        this.comentarioResolucion = comentarioResolucion;
    }

    public String getComentarioCierre() {
        return comentarioCierre;
    }

    public void setComentarioCierre(String comentarioCierre) {
        this.comentarioCierre = comentarioCierre;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Cuenta getOperadorCierre() {
        return operadorCierre;
    }

    public void setOperadorCierre(Cuenta operadorCierre) {
        this.operadorCierre = operadorCierre;
    }

    public String getComentarioRechazo() {
        return comentarioRechazo;
    }

    public void setComentarioRechazo(String comentarioRechazo) {
        this.comentarioRechazo = comentarioRechazo;
    }

    public LocalDateTime getFechaRechazo() {
        return fechaRechazo;
    }

    public void setFechaRechazo(LocalDateTime fechaRechazo) {
        this.fechaRechazo = fechaRechazo;
    }

    public Cuenta getOperadorRechazo() {
        return operadorRechazo;
    }

    public void setOperadorRechazo(Cuenta operadorRechazo) {
        this.operadorRechazo = operadorRechazo;
    }

    public Integer getContadorRechazos() {
        return contadorRechazos;
    }

    public void setContadorRechazos(Integer contadorRechazos) {
        this.contadorRechazos = contadorRechazos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Reporte))
            return false;
        Reporte reporte = (Reporte) o;
        return Objects.equals(id, reporte.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
