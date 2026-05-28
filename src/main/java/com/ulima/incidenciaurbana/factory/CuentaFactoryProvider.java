package com.ulima.incidenciaurbana.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proveedor de factories - Patrón Abstract Factory
 * Selecciona la factory correcta según el tipo de cuenta solicitado
 */
@Component
public class CuentaFactoryProvider {
    
    private final Map<String, CuentaFactory> factories = new HashMap<>();
    
    @Autowired
    public CuentaFactoryProvider(List<CuentaFactory> factoryList) {
        // Registra automáticamente todas las factories disponibles
        for (CuentaFactory factory : factoryList) {
            factories.put(factory.getTipoCuenta(), factory);
        }
    }
    
    /**
     * Obtiene la factory correspondiente al tipo de cuenta
     * @param tipoCuenta CIUDADANO, TECNICO, OPERADOR_MUNICIPAL
     * @return factory correspondiente
     * @throws IllegalArgumentException si el tipo de cuenta no es válido
     */
    public CuentaFactory getFactory(String tipoCuenta) {
        CuentaFactory factory = factories.get(tipoCuenta.toUpperCase());
        if (factory == null) {
            throw new IllegalArgumentException(
                "Tipo de cuenta no válido: " + tipoCuenta + 
                ". Tipos válidos: CIUDADANO, TECNICO, OPERADOR_MUNICIPAL"
            );
        }
        return factory;
    }
    
    /**
     * Verifica si un tipo de cuenta es válido
     */
    public boolean esValido(String tipoCuenta) {
        return factories.containsKey(tipoCuenta.toUpperCase());
    }
}
