package com.ulima.incidenciaurbana.factory;

import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.Persona;
import com.ulima.incidenciaurbana.model.Tecnico;
import org.springframework.stereotype.Component;

/**
 * Factory concreta para crear cuentas de tipo Tecnico
 */
@Component
public class TecnicoFactory implements CuentaFactory {
    
    @Override
    public Cuenta crearCuenta(String usuario, String contrasenaHash, Persona persona) {
        return new Tecnico(usuario, contrasenaHash, persona);
    }
    
    @Override
    public String getTipoCuenta() {
        return "TECNICO";
    }
}
