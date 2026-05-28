package com.ulima.incidenciaurbana.service;

/**
 * Servicio para gestionar almacenamiento en Firebase Storage
 * Permite subir, descargar y eliminar archivos en la nube
 */
public interface IFirebaseStorageService {

    /**
     * Sube un archivo a Firebase Storage
     *
     * @param fileName  nombre del archivo
     * @param fileBytes contenido del archivo en bytes
     * @param bucket    nombre del bucket (opcional, usa default si es null)
     * @return URL pública del archivo subido
     * @throws RuntimeException si hay error en la subida
     */
    String uploadFile(String fileName, byte[] fileBytes, String bucket);

    /**
     * Sube un archivo a Firebase Storage (usa bucket por defecto)
     *
     * @param fileName  nombre del archivo
     * @param fileBytes contenido del archivo en bytes
     * @return URL pública del archivo subido
     */
    String uploadFile(String fileName, byte[] fileBytes);

    /**
     * Elimina un archivo de Firebase Storage
     *
     * @param fileName nombre del archivo
     * @param bucket   nombre del bucket (opcional, usa default si es null)
     * @throws RuntimeException si hay error en la eliminación
     */
    void deleteFile(String fileName, String bucket);

    /**
     * Elimina un archivo de Firebase Storage (usa bucket por defecto)
     *
     * @param fileName nombre del archivo
     */
    void deleteFile(String fileName);

    /**
     * Verifica si Firebase Storage está disponible
     *
     * @return true si está configurado correctamente
     */
    boolean isAvailable();
}
