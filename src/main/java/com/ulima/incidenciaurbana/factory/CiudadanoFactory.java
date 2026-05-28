package com.ulima.incidenciaurbana.factory;

import com.ulima.incidenciaurbana.model.Ciudadano;
import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.Persona;
import org.springframework.stereotype.Component;

/**
 * Factory concreta para crear cuentas de tipo Ciudadano
 */
@Component
public class CiudadanoFactory implements CuentaFactory {
    
    @Override
    public Cuenta crearCuenta(String usuario, String contrasenaHash, Persona persona) {
        return new Ciudadano(usuario, contrasenaHash, persona);
    }
    
    @Override
    public String getTipoCuenta() {
        return "CIUDADANO";
    }
}
