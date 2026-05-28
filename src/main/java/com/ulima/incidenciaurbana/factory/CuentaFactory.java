package com.ulima.incidenciaurbana.factory;

import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.Persona;

/**
 * Abstract Factory para la creación de diferentes tipos de cuentas
 * Implementa el patrón Abstract Factory para crear Ciudadano, Tecnico u OperadorMunicipal
 */
public interface CuentaFactory {
    /**
     * Método factory para crear una cuenta específica
     * @param usuario nombre de usuario
     * @param contrasenaHash contraseña hasheada
     * @param persona datos de la persona
     * @return instancia de Cuenta (Ciudadano, Tecnico u OperadorMunicipal)
     */
    Cuenta crearCuenta(String usuario, String contrasenaHash, Persona persona);
    
    /**
     * Retorna el tipo de cuenta que crea esta factory
     * @return tipo de cuenta (CIUDADANO, TECNICO, OPERADOR_MUNICIPAL)
     */
    String getTipoCuenta();
}
