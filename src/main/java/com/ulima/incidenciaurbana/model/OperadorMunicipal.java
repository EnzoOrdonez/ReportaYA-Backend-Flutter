package com.ulima.incidenciaurbana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "operadores_municipales")
public class OperadorMunicipal extends Cuenta {

    public OperadorMunicipal() {
        super();
    }

    public OperadorMunicipal(String usuario, String contrasenaHash, Persona persona) {
        super(usuario, contrasenaHash, persona);
    }

    public void asignarPrioridad(Reporte reporte, Prioridad prioridad) {
        if (reporte != null && prioridad != null) {
            reporte.setPrioridad(prioridad);
        }
    }

    public Asignacion asignarTecnico(Reporte reporte, Tecnico tecnico) {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }
        if (tecnico == null) {
            throw new IllegalArgumentException("El técnico no puede ser nulo");
        }
        Asignacion asignacion = new Asignacion(reporte, this, tecnico);
        return asignacion;
    }

    // OperadorMunicipal delega a los servicios para operaciones de lectura paginada.
    @Override
    public String getTipoCuenta() {
        return "OPERADOR_MUNICIPAL";
    }
}
