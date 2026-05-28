package com.ulima.incidenciaurbana.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                ClassPathResource resource = new ClassPathResource("firebase-service-account.json");

                // Verificar si el archivo existe
                if (!resource.exists()) {
                    System.err.println("⚠️  WARNING: firebase-service-account.json no encontrado.");
                    System.err.println("⚠️  Las notificaciones Firebase NO estarán disponibles.");
                    System.err.println("⚠️  La aplicación continuará funcionando sin notificaciones push.");
                    return null; // Retornar null para permitir que la app inicie
                }

                InputStream serviceAccount = resource.getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                System.out.println("✅ Firebase inicializado correctamente.");
                return FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                System.err.println("⚠️  ERROR al inicializar Firebase: " + e.getMessage());
                System.err.println("⚠️  La aplicación continuará sin notificaciones push.");
                return null;
            }
        }
        return FirebaseApp.getInstance();
    }
}
