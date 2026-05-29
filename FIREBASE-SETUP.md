# 🔥 Configuración de Firebase (Opcional)

## ⚠️ Notificaciones Push Deshabilitadas por Defecto

La aplicación **funciona completamente sin Firebase**. Las notificaciones push son **opcionales** y solo se requieren si deseas enviar notificaciones a dispositivos móviles.

---

## 📱 ¿Qué funciona sin Firebase?

✅ **TODO el sistema funciona normalmente:**
- Creación de cuentas (Ciudadano, Técnico, Operador)
- Creación y gestión de reportes
- Asignación de técnicos
- Completar reportes con fotos
- Cerrar/rechazar reportes (auditoría)
- **Historial de estados** (auditoría completa)
- Registro de tokens FCM (se guardan en la BD)

❌ **Lo único que NO funciona:**
- Envío de notificaciones push a dispositivos móviles

---

## 🔧 Cómo habilitar Firebase (si lo necesitas)

### Paso 1: Obtener credenciales de Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Ve a **Project Settings** (⚙️ → Configuración del proyecto)
4. Pestaña **Service Accounts**
5. Click en **Generate new private key**
6. Descarga el archivo JSON

### Paso 2: Configurar el proyecto

1. Renombra el archivo descargado a: `firebase-service-account.json`
2. Colócalo en: `src/main/resources/firebase-service-account.json`

```
ReportaYA-Backend/
└── src/
    └── main/
        └── resources/
            ├── application.properties
            └── firebase-service-account.json  ← Aquí
```

### Paso 3: Reiniciar la aplicación

```bash
mvn spring-boot:run
```

Verás el mensaje:
```
✅ Firebase inicializado correctamente.
```

---

## 🧪 Modo de Prueba (Sin Firebase)

Cuando Firebase **NO** está configurado, las notificaciones se **simulan en consola**:

```
⚠️  Firebase no disponible. Notificación simulada:
   📱 Token: fcm_token_ciudadano...
   📧 Título: Actualización de Reporte
   💬 Mensaje: El estado de tu reporte cambió a PROCESO
```

Esto permite:
- ✅ Probar todo el flujo de la aplicación
- ✅ Verificar que los tokens se registren en la BD
- ✅ Ver qué notificaciones se enviarían
- ✅ Desarrollar sin depender de Firebase

---

## 🔒 Seguridad

**IMPORTANTE**: El archivo `firebase-service-account.json` contiene **credenciales privadas**.

✅ **YA está en `.gitignore`** - No se subirá a Git
❌ **NUNCA** lo subas a GitHub, GitLab, etc.
🔐 Cada desarrollador debe obtener su propia copia

---

## 📊 Verificar si Firebase está activo

### Opción 1: Logs al iniciar

```
✅ Firebase inicializado correctamente.        ← Activo
⚠️  Firebase no disponible.                   ← Inactivo
```

### Opción 2: Probar endpoint de notificaciones

```http
POST http://localhost:8081/notificaciones/enviar-prueba
  ?token=test_token_123
  &titulo=Prueba
  &mensaje=Hola
```

**Con Firebase:**
```
✅ Notificación enviada exitosamente: projects/.../messages/...
```

**Sin Firebase:**
```
⚠️  Firebase no disponible. Notificación simulada:
   📱 Token: test_token_123
   📧 Título: Prueba
   💬 Mensaje: Hola
```

---

## 🎯 Resumen

| Feature | Sin Firebase | Con Firebase |
|---------|--------------|--------------|
| API REST | ✅ Funciona | ✅ Funciona |
| Historial de Estados | ✅ Funciona | ✅ Funciona |
| Registro de Tokens | ✅ Funciona | ✅ Funciona |
| Push Notifications | ❌ Simuladas | ✅ Reales |

**Conclusión**: Firebase es **100% opcional** para desarrollo y testing. Solo necesario para producción con notificaciones móviles.
