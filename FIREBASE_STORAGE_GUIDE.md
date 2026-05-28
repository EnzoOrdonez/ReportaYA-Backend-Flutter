# 📸 Almacenamiento de Fotos con Firebase Storage

## 🎯 Descripción

El sistema ha sido actualizado para almacenar las fotos que suben los técnicos directamente en **Firebase Storage** (nube), en lugar de guardarlas localmente. Esto permite que:

✅ Las fotos sean accesibles desde cualquier lugar  
✅ El operador pueda ver las fotos durante la auditoría  
✅ Se escale automáticamente sin preocupación de espacio en disco  
✅ Las fotos sean seguras y respaldadas

---

## 🔧 Configuración Requerida

### 1. Archivo de Credenciales Firebase

Necesitas el archivo `firebase-service-account.json` que debe contener:

```json
{
  "type": "service_account",
  "project_id": "tu-proyecto-firebase",
  "private_key_id": "...",
  "private_key": "...",
  "client_email": "firebase-adminsdk-xxxxx@tu-proyecto-firebase.iam.gserviceaccount.com",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "..."
}
```

**Ubicación:** `src/main/resources/firebase-service-account.json`

**Obtenerlo:** Desde [Firebase Console](https://console.firebase.google.com/) → Project Settings → Service Accounts → Generate New Private Key

### 2. Bucket de Firebase Storage

En tu proyecto Firebase, debe estar habilitado **Firebase Storage** con bucket por defecto: `tu-proyecto.appspot.com`

---

## 📤 Flujo de Funcionamiento

### Cuando un Técnico Completa un Reporte

```http
PATCH /api/tecnicos/{tecnicoId}/reportes/{reporteId}/completar
Content-Type: application/json

{
  "comentarioResolucion": "Reparación completada exitosamente",
  "fotos": [
    {
      "tipo": "INICIAL",
      "descripcion": "Foto inicial del problema",
      "archivoBase64": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
    },
    {
      "tipo": "PROCESO",
      "descripcion": "Foto del proceso de reparación",
      "archivoBase64": "..."
    },
    {
      "tipo": "FINAL",
      "descripcion": "Foto final del trabajo",
      "archivoBase64": "..."
    }
  ]
}
```

### Proceso Interno

1. **Decodificación**: Se decodifica cada foto de base64 a bytes
2. **Detección de formato**: Se detecta automáticamente si es PNG, JPEG o WebP
3. **Subida a Firebase**:
   - Si Firebase está disponible → Se sube a la nube
   - Si no está disponible → Se guarda localmente como fallback
4. **Almacenamiento en BD**: Se registra la URL en la tabla `Foto`
5. **Acceso**: El operador obtiene la URL pública para ver la foto

---

## 🔗 Estructura de URLs

### URLs en Firebase Storage

```
https://firebasestorage.googleapis.com/v0/b/reportaya-backend.appspot.com/o/fotos/reporte-{id}-{tipo}-{uuid}.{ext}?alt=media
```

**Ejemplo:**
```
https://firebasestorage.googleapis.com/v0/b/reportaya-backend.appspot.com/o/fotos/reporte-2-INICIAL-abc123.jpg?alt=media
```

**Ventajas:**
- ✅ URL pública y accesible
- ✅ Sin autenticación requerida (por defecto)
- ✅ Cacheable en CDN de Google
- ✅ Disponible en cualquier dispositivo/ubicación

---

## 📊 Servicio de Almacenamiento

### Interfaz: `IFirebaseStorageService`

```java
// Subir archivo (bucket por defecto)
String uploadFile(String fileName, byte[] fileBytes);

// Subir a bucket específico
String uploadFile(String fileName, byte[] fileBytes, String bucket);

// Eliminar archivo
void deleteFile(String fileName);

// Verificar disponibilidad
boolean isAvailable();
```

### Inyección en Servicios

```java
@Service
public class TecnicoServiceImpl implements ITecnicoService {
    
    private final IFirebaseStorageService firebaseStorageService;
    
    public TecnicoServiceImpl(IFirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }
    
    // Uso:
    if (firebaseStorageService.isAvailable()) {
        String url = firebaseStorageService.uploadFile(fileName, bytes);
    }
}
```

---

## 🚀 Ejemplo Completo: Endpoint de Completar Reporte

```java
@PatchMapping("/{tecnicoId}/reportes/{reporteId}/completar")
public ResponseEntity<ReporteDTO> completarReporte(
        @PathVariable Long tecnicoId,
        @PathVariable Long reporteId,
        @RequestBody CompletarReporteRequest request) {
    
    // Técnico sube fotos en base64
    ReporteDTO resultado = tecnicoService.completarReporte(
        tecnicoId, reporteId, request);
    
    // Las fotos se suben a Firebase Storage automáticamente
    // Las URLs se almacenan en la BD
    
    return ResponseEntity.ok(resultado);
}
```

---

## 🔍 Cómo Verificar que Funciona

### 1. Verificar en Logs

```
✅ Archivo subido exitosamente a Firebase Storage: fotos/reporte-2-INICIAL-abc123.jpg
```

### 2. Ver en Firebase Console

1. Ir a [Firebase Console](https://console.firebase.google.com/)
2. Seleccionar proyecto → Storage
3. Ver carpeta `fotos/` con archivos subidos

### 3. Acceder a la Foto

Desde el endpoint de obtener reporte, la URL estará disponible:

```json
{
  "id": 2,
  "titulo": "Poste dañado",
  "estado": "RESUELTA",
  "fotos": [
    {
      "id": 1,
      "tipo": "INICIAL",
      "url": "https://firebasestorage.googleapis.com/v0/b/reportaya-backend.appspot.com/o/fotos/reporte-2-INICIAL-abc123.jpg?alt=media",
      "descripcion": "Foto inicial del problema"
    }
  ]
}
```

---

## ⚠️ Fallback a Almacenamiento Local

Si Firebase Storage **NO está disponible** (archivo de credenciales faltante), el sistema:

1. **Detecta** que Firebase no está disponible con `isAvailable()`
2. **Guarda localmente** en `uploads/fotos/` como fallback
3. **Continúa funcionando** sin interrupciones
4. **Muestra advertencia** en logs

```
⚠️  Firebase no disponible. Foto guardada localmente: fotos/reporte-2-INICIAL-abc123.jpg
```

---

## 🛡️ Seguridad

### Reglas de Firebase Storage (Recomendado)

Para que solo usuarios autenticados suban fotos:

```rules
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /fotos/{allPaths=**} {
      // Solo lectura pública
      allow read: if true;
      
      // Solo escritura si está autenticado
      allow write: if request.auth != null;
    }
  }
}
```

### URLs Públicas

Por defecto, las URLs generadas son públicas para que el operador pueda verlas. Si necesitas URLs privadas con control de acceso:

- Genera URLs firmadas (Signed URLs)
- Implementa autenticación en el frontend

---

## 📝 Cambios en el Código

### Antes (Almacenamiento Local)

```java
Path dirPath = Paths.get("uploads/fotos");
Files.createDirectories(dirPath);
Path filePath = dirPath.resolve(nombreArchivo);
Files.write(filePath, decodedBytes);
foto.setUrl("uploads/fotos/" + nombreArchivo);
```

### Después (Firebase Storage)

```java
if (firebaseStorageService.isAvailable()) {
    String urlFoto = firebaseStorageService.uploadFile(nombreArchivo, decodedBytes);
} else {
    // Fallback local
    Files.write(filePath, decodedBytes);
    urlFoto = "uploads/fotos/" + nombreArchivo;
}
foto.setUrl(urlFoto);
```

---

## 🐛 Troubleshooting

| Problema | Causa | Solución |
|----------|-------|----------|
| `Firebase Storage no disponible` | Falta `firebase-service-account.json` | Agregar archivo en `src/main/resources/` |
| `Permission denied` | Credenciales sin permisos | Regenerar credenciales desde Firebase Console |
| `Bucket not found` | Nombre de bucket incorrecto | Verificar en Firebase Console |
| `URL 404` | La foto se guardó local en lugar de Firebase | Verificar logs y `isAvailable()` |

---

## 📚 Referencias

- [Firebase Storage Documentation](https://firebase.google.com/docs/storage)
- [Firebase Admin SDK for Java](https://firebase.google.com/docs/database/admin/start)
- [Cloud Storage for Firebase - Best Practices](https://firebase.google.com/docs/storage/security)

---

**Versión:** 1.0  
**Fecha:** 2025-11-23  
**Proyecto:** ReportaYA-Backend
