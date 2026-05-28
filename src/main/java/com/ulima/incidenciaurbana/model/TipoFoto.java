package com.ulima.incidenciaurbana.model;

/**
 * Enum para clasificar el tipo de foto en el ciclo de vida del reporte
 */
public enum TipoFoto {
    INICIAL, // Foto tomada al inicio del trabajo (antes)
    PROCESO, // Foto durante el trabajo (evidencia de progreso)
    FINAL // Foto al completar el trabajo (después)
}
