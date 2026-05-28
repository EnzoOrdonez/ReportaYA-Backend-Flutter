package com.ulima.incidenciaurbana.dto;

/**
 * DTO para representar un técnico en respuestas de la API
 * Se usa para listar técnicos disponibles, asignarles reportes, etc.
 */
public class TecnicoDTO {

    private Long id;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String email;
    private String especialidad; // Campo opcional si existe en la entidad Tecnico

    // Constructores
    public TecnicoDTO() {
    }

    public TecnicoDTO(Long id, String usuario, String nombres, String apellidos, String email) {
        this.id = id;
        this.usuario = usuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
    }

    public TecnicoDTO(Long id, String usuario, String nombres, String apellidos, String email, String especialidad) {
        this(id, usuario, nombres, apellidos, email);
        this.especialidad = especialidad;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "TecnicoDTO{" +
                "id=" + id +
                ", usuario='" + usuario + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}
