package com.ulima.incidenciaurbana.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import com.ulima.incidenciaurbana.service.IFirebaseStorageService;
import org.springframework.stereotype.Service;

/**
 * Implementación de almacenamiento en Firebase Storage
 * Proporciona métodos para subir, descargar y eliminar archivos de la nube
 */
@Service
public class FirebaseStorageServiceImpl implements IFirebaseStorageService {

    private static final String DEFAULT_BUCKET = "reportaya-backend.appspot.com";

    /**
     * Sube un archivo a Firebase Storage
     */
    @Override
    public String uploadFile(String fileName, byte[] fileBytes, String bucket) {
        try {
            // Usar bucket por defecto si no se especifica
            String targetBucket = bucket != null ? bucket : DEFAULT_BUCKET;

            // Obtener cliente de Storage
            Storage storage = StorageClient.getInstance().bucket(targetBucket).getStorage();

            // Crear BlobId y BlobInfo
            BlobId blobId = BlobId.of(targetBucket, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("application/octet-stream")
                    .build();

            // Crear blob (archivo) en Storage
            storage.create(blobInfo, fileBytes);

            System.out.println(" Archivo subido exitosamente a Firebase Storage: " + fileName);

            // Generar URL pública del archivo
            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    targetBucket,
                    fileName.replace("/", "%2F"));

        } catch (Exception e) {
            System.err.println(" Error al subir archivo a Firebase Storage: " + e.getMessage());
            throw new RuntimeException("Error al subir archivo a Firebase Storage: " + e.getMessage(), e);
        }
    }

    /**
     * Sube un archivo a Firebase Storage (usa bucket por defecto)
     */
    @Override
    public String uploadFile(String fileName, byte[] fileBytes) {
        return uploadFile(fileName, fileBytes, DEFAULT_BUCKET);
    }

    /**
     * Elimina un archivo de Firebase Storage
     */
    @Override
    public void deleteFile(String fileName, String bucket) {
        try {
            // Usar bucket por defecto si no se especifica
            String targetBucket = bucket != null ? bucket : DEFAULT_BUCKET;

            // Obtener cliente de Storage
            Storage storage = StorageClient.getInstance().bucket(targetBucket).getStorage();

            // Crear BlobId y eliminar
            BlobId blobId = BlobId.of(targetBucket, fileName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                System.out.println("Archivo eliminado exitosamente de Firebase Storage: " + fileName);
            } else {
                System.out.println("El archivo no existe en Firebase Storage: " + fileName);
            }

        } catch (Exception e) {
            System.err.println("Error al eliminar archivo de Firebase Storage: " + e.getMessage());
            throw new RuntimeException("Error al eliminar archivo de Firebase Storage: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un archivo de Firebase Storage (usa bucket por defecto)
     */
    @Override
    public void deleteFile(String fileName) {
        deleteFile(fileName, DEFAULT_BUCKET);
    }

    /**
     * Verifica si Firebase Storage está disponible
     */
    @Override
    public boolean isAvailable() {
        try {
            StorageClient.getInstance().bucket(DEFAULT_BUCKET);
            return true;
        } catch (Exception e) {
            System.err.println("Firebase Storage no está disponible: " + e.getMessage());
            return false;
        }
    }
}
