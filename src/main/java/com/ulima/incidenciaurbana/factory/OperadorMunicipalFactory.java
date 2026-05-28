package com.ulima.incidenciaurbana.factory;

import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.OperadorMunicipal;
import com.ulima.incidenciaurbana.model.Persona;
import org.springframework.stereotype.Component;

/**
 * Factory concreta para crear cuentas de tipo OperadorMunicipal
 */
@Component
public class OperadorMunicipalFactory implements CuentaFactory {
    
    @Override
    public Cuenta crearCuenta(String usuario, String contrasenaHash, Persona persona) {
        return new OperadorMunicipal(usuario, contrasenaHash, persona);
    }
    
    @Override
    public String getTipoCuenta() {
        return "OPERADOR_MUNICIPAL";
    }
}
