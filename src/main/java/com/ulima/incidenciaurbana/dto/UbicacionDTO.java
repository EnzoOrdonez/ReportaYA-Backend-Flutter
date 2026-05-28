package com.ulima.incidenciaurbana.dto;

import java.time.LocalDateTime;

public class UbicacionDTO {
    private Long id;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private LocalDateTime fechaRegistro;
    
    // Constructors
    public UbicacionDTO() {
    }
    
    public UbicacionDTO(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    public UbicacionDTO(Long id, Double latitud, Double longitud, String direccion, LocalDateTime fechaRegistro) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
    
    public Double getLongitud() {
        return longitud;
    }
    
    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
