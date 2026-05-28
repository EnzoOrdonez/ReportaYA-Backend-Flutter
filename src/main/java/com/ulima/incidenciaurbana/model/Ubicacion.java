package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    // Dirección completa (legacy/backup)
    @Column(length = 500)
    private String direccion;

    // ===== CAMPOS SEPARADOS PARA DIRECCIÓN =====

    @Column(length = 200)
    private String calle; // "Javier Prado Este"

    @Column(length = 100)
    private String distrito; // "Santiago de Surco", "San Borja", etc.

    @Column(length = 100)
    private String ciudad; // "Lima"

    @Column(length = 100)
    private String departamento; // "Lima"

    @Column(length = 100)
    private String pais; // "Perú"

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructors
    public Ubicacion() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Ubicacion(Double latitud, Double longitud) {
        this();
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Ubicacion(Double latitud, Double longitud, String direccion) {
        this(latitud, longitud);
        this.direccion = direccion;
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

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ubicacion))
            return false;
        Ubicacion ubicacion = (Ubicacion) o;
        return Objects.equals(id, ubicacion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ubicacion{" +
                "id=" + id +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", calle='" + calle + '\'' +
                ", distrito='" + distrito + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", departamento='" + departamento + '\'' +
                ", pais='" + pais + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
