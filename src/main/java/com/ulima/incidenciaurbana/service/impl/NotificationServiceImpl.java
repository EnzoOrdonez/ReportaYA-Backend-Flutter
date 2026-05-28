package com.ulima.incidenciaurbana.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ulima.incidenciaurbana.dto.TokenNotificacionDTO;
import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.TokenNotificacion;
import com.ulima.incidenciaurbana.repository.CuentaRepository;
import com.ulima.incidenciaurbana.repository.TokenNotificacionRepository;
import com.ulima.incidenciaurbana.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationService {

    private final TokenNotificacionRepository tokenRepository;
    private final CuentaRepository cuentaRepository;

    @Autowired
    public NotificationServiceImpl(TokenNotificacionRepository tokenRepository, CuentaRepository cuentaRepository) {
        this.tokenRepository = tokenRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public void registrarToken(TokenNotificacionDTO tokenDTO) {
        if (tokenDTO.getCuentaId() == null) {
            throw new RuntimeException("El ID de la cuenta es obligatorio");
        }
        long cuentaId = tokenDTO.getCuentaId();
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + cuentaId));

        Optional<TokenNotificacion> tokenExistente = tokenRepository.findByCuentaId(cuentaId);

        if (tokenExistente.isPresent()) {
            TokenNotificacion token = tokenExistente.get();
            token.setToken(tokenDTO.getToken());
            tokenRepository.save(token);
        } else {
            TokenNotificacion nuevoToken = new TokenNotificacion(cuenta, tokenDTO.getToken());
            tokenRepository.save(nuevoToken);
        }
    }

    @Override
    public void enviarNotificacion(Long cuentaId, String titulo, String mensaje) {
        if (cuentaId == null)
            return;

        Optional<TokenNotificacion> tokenOpt = tokenRepository.findByCuentaId(cuentaId);
        if (tokenOpt.isPresent()) {
            enviarNotificacionAUnToken(tokenOpt.get().getToken(), titulo, mensaje);
        } else {
            System.err.println("No se encontró token para la cuenta: " + cuentaId);
        }
    }

    @Override
    public void enviarNotificacionAUnToken(String token, String titulo, String mensaje) {
        try {
            // Verificar si Firebase está disponible
            if (com.google.firebase.FirebaseApp.getApps().isEmpty()) {
                System.out.println("⚠️  Firebase no disponible. Notificación simulada:");
                System.out.println("   📱 Token: " + token.substring(0, Math.min(20, token.length())) + "...");
                System.out.println("   📧 Título: " + titulo);
                System.out.println("   💬 Mensaje: " + mensaje);
                return;
            }

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Notificación enviada exitosamente: " + response);
        } catch (Exception e) {
            System.err.println("⚠️  Error enviando notificación al token " + token + ": " + e.getMessage());
            System.err.println("   La aplicación continuará funcionando normalmente.");
        }
    }
}
