package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ciudadanos")
public class Ciudadano extends Cuenta {
    
    // Constructors
    public Ciudadano() {
        super();
    }
    
    public Ciudadano(String usuario, String contrasenaHash, Persona persona) {
        super(usuario, contrasenaHash, persona);
    }
    
    // Business Methods
    @Override
    public void crearReporte(Reporte reporte) {
        super.crearReporte(reporte);
    }
    
    public void consultarEstado() {
        // Lógica para consultar estado de reportes
        System.out.println("Consultando estado de reportes del ciudadano: " + getPersona().getNombreCompleto());
    }

    @Override
    public String getTipoCuenta() {
        return "CIUDADANO";
    }
}
