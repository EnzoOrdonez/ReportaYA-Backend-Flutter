package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.CuentaDTO;

/**
 * Interfaz del servicio unificado para creación de cuentas
 * Usa Abstract Factory para crear diferentes tipos de cuentas
 */
public interface ICuentaService {
    /**
     * Crea una cuenta de cualquier tipo usando Abstract Factory
     * @param cuentaDTO datos de la cuenta a crear
     * @return datos de la cuenta creada
     * @throws IllegalArgumentException si el tipo de cuenta no es válido
     * @throws RuntimeException si hay errores de duplicación o validación
     */
    CuentaDTO crearCuenta(CuentaDTO cuentaDTO);
}
