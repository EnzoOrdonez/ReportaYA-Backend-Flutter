package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "personas")
public class Persona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombres;
    
    @Column(nullable = false)
    private String apellidos;
    
    @Column(nullable = false, unique = true)
    private String dni;
    
    @Column(nullable = false)
    private String telefono;
    
    @Column(nullable = false, unique = true)
    private String correo;
    
    // Constructors
    public Persona() {
    }
    
    public Persona(String nombres, String apellidos, String dni, String telefono, String correo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
    }
    
    // Business Methods
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
    
    public void actualizarDatos(String telefono, String correo) {
        this.telefono = telefono;
        this.correo = correo;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombres() {
        return nombres;
    }
    
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    
    public String getApellidos() {
        return apellidos;
    }
    
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona persona = (Persona) o;
        return Objects.equals(id, persona.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
