package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.TokenNotificacionDTO;
import com.ulima.incidenciaurbana.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificaciones")
public class NotificationController {

    private final INotificationService notificationService;

    @Autowired
    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/registrar-token")
    public ResponseEntity<String> registrarToken(@RequestBody TokenNotificacionDTO tokenDTO) {
        notificationService.registrarToken(tokenDTO);
        return ResponseEntity.ok("Token registrado exitosamente");
    }

    @PostMapping("/enviar-prueba")
    public ResponseEntity<String> enviarPrueba(@RequestParam String token, @RequestParam String titulo, @RequestParam String mensaje) {
        notificationService.enviarNotificacionAUnToken(token, titulo, mensaje);
        return ResponseEntity.ok("Intento de envío realizado. Revisa la consola del backend.");
    }
}
