package com.ulima.incidenciaurbana.model;

public enum EstadoReporte {
    PENDIENTE,
    REVISION,
    PROCESO,
    RESUELTA,
    CERRADA,
    RECHAZADO_AUDITO, // Rechazo de auditoría - técnico puede reintentar
    RECHAZADO // Rechazo definitivo - sin reintentos
}
