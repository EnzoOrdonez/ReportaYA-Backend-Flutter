package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.TokenNotificacionDTO;

public interface INotificationService {
    void registrarToken(TokenNotificacionDTO tokenDTO);
    void enviarNotificacion(Long cuentaId, String titulo, String mensaje);
    void enviarNotificacionAUnToken(String token, String titulo, String mensaje);
}
