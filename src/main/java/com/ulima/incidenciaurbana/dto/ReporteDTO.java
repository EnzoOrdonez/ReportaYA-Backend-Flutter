package com.ulima.incidenciaurbana.dto;

import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.Prioridad;
import com.ulima.incidenciaurbana.model.TipoProblema;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Long cuentaId;
    private String nombreCiudadano;
    private Prioridad prioridad;
    private EstadoReporte estado;
    private TipoProblema tipoProblema;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private UbicacionDTO ubicacion;

    // Campos de Evidencias (ESCENARIO 1 - Técnico)
    private List<FotoDTO> fotos;
    private String comentarioResolucion;

    // Campos de Auditoría (ESCENARIO 2 y 3)
    private String comentarioCierre;
    private LocalDateTime fechaCierre;
    private CuentaDTO operadorCierre;

    private String comentarioRechazo;
    private LocalDateTime fechaRechazo;
    private CuentaDTO operadorRechazo;

    private Integer contadorRechazos;

    // Campos de Asignación (Técnico asignado)
    private Long tecnicoId;
    private String tecnicoNombre;

    // Constructors
    public ReporteDTO() {
    }

    public ReporteDTO(Long id, String titulo, String descripcion, Long cuentaId, String nombreCiudadano,
            Prioridad prioridad, EstadoReporte estado, TipoProblema tipoProblema) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cuentaId = cuentaId;
        this.nombreCiudadano = nombreCiudadano;
        this.prioridad = prioridad;
        this.estado = estado;
        this.tipoProblema = tipoProblema;
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

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getNombreCiudadano() {
        return nombreCiudadano;
    }

    public void setNombreCiudadano(String nombreCiudadano) {
        this.nombreCiudadano = nombreCiudadano;
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

    public UbicacionDTO getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(UbicacionDTO ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<FotoDTO> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoDTO> fotos) {
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

    public CuentaDTO getOperadorCierre() {
        return operadorCierre;
    }

    public void setOperadorCierre(CuentaDTO operadorCierre) {
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

    public CuentaDTO getOperadorRechazo() {
        return operadorRechazo;
    }

    public void setOperadorRechazo(CuentaDTO operadorRechazo) {
        this.operadorRechazo = operadorRechazo;
    }

    public Integer getContadorRechazos() {
        return contadorRechazos;
    }

    public void setContadorRechazos(Integer contadorRechazos) {
        this.contadorRechazos = contadorRechazos;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    public void setTecnicoNombre(String tecnicoNombre) {
        this.tecnicoNombre = tecnicoNombre;
    }
}
