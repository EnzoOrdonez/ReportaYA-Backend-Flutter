# 📋 Análisis Completo: Cierre de Reporte con Evidencias y Auditoría

## 📌 Contexto de Negocio

```
FLUJO ANTERIOR (Sin evidencias):
Técnico resuelve → Estado RESUELTA → Operador audita estado

FLUJO NUEVO (Con evidencias):
Técnico resuelve + adjunta fotos + escribe comentario 
  → Estado RESUELTA 
  → Operador audita EVIDENCIAS 
  → Aprueba (CERRADA) o Rechaza (RECHAZADO_AUDITO)
```

---

## 🔄 Nuevo Flujo Completo

```
┌────────────────────────────────────────────────────────────────────┐
│                    CICLO COMPLETO MEJORADO                         │
└────────────────────────────────────────────────────────────────────┘

┌──────────────────┐
│   PENDIENTE      │  Ciudadano crea reporte
│ • titulo         │
│ • descripcion    │
│ • ubicacion      │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────┐
│   REVISION               │  Operador revisa y asigna
│ • prioridad (NUEVA)      │
│ • técnico (ASIGNADO)     │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│   PROCESO                                │  Técnico trabaja
│ • asignacion activa                      │
│ • operador + técnico asignado            │
│ • Técnico adjunta FOTOS (NUEVA)          │ ← EVIDENCIAS
│ • Técnico escribe COMENTARIO (NUEVA)     │   DEL TRABAJO
└────────┬─────────────────────────────────┘
         │
         ▼
┌───────────────────────────────────────────────────────────────┐
│   RESUELTA (Técnico completó trabajo)                         │
│ • comentario_resolucion (NUEVO) ← Qué hizo                    │
│ • fotos/evidencias (NUEVO) ← Pruebas de trabajo               │
│ • estado cambió automáticamente                               │
└────────┬──────────────────────────────────────────────────────┘
         │
         │  Operador abre "Control de Calidad"
         │  y VE las evidencias
         │
         ├─────────────────────────────┬──────────────────────────┐
         │                             │                          │
         ▼                             ▼                          ▼
    ✅ VÁLIDO               ⚠️ DUDA                    ❌ RECHAZAR
    (Ver fotos)         (Revisar fotos)            (Fotos insuficientes)
         │                    │                          │
         ▼                    ▼                          ▼
    ┌─────────────┐    ┌─────────────┐    ┌──────────────────────────┐
    │   CERRADA   │    │   PROCESO   │    │ RECHAZADO_AUDITO (NUEVO) │
    │ ✅ APROBADO │    │ (Reasignar) │   │  ❌ AUDITORÍA RECHAZADA  │
    │             │    │             │    │                          │
    │ Operador OK │    │ Más trabajo │    │ • comentario_rechazo     │
    │             │    │             │    │ • fecha_rechazo          │
    └─────────────┘    └─────────────┘    │ • operador_rechazo_id    │
         ↓                   ↓            └──────────────────────────┘
      ENVIADA                                      ↓
     NOTIFICACIÓN           Técnico reenvía    ENVIADA
    AL CIUDADANO           con nuevas fotos   NOTIFICACIÓN
                                ↓             AL TÉCNICO
                            (Vuelve a PROCESO)
                                ↓
                            (Luego → RESUELTA → CERRADA)
```

---

## 🗂️ Nuevos Estados

```
ENUM EstadoReporte (ACTUALIZADO):

PENDIENTE                    → Inicial
  ↓
REVISION                     → Operador revisa
  ↓
PROCESO                      → Técnico trabaja
  ↓
RESUELTA                     → Técnico completó
  ├─→ CERRADA               → Operador aprobó ✅
  ├─→ RECHAZADO_AUDITO (NEW) → Operador rechazó ❌
  │                           (Técnico debe rehacer)
  └─→ RECHAZADO             → Operador rechaza definitivamente
       (Ciudadano notificado, cierre)
```

---

## 💾 Campos Nuevos por Entidad

### 1. REPORTE (Cambios para almacenar evidencias y auditoría)

```
Campos NUEVOS para resolución (añadido por TÉCNICO):
├─ comentario_resolucion VARCHAR(1000)
│  └─ Qué hizo el técnico para resolver
│
└─ fotos (NUEVO - Relación con tabla FOTOS)
   └─ Evidencias fotográficas

Campos NUEVOS para auditoría (Aprobación):
├─ comentario_cierre VARCHAR(1000)
├─ fecha_cierre TIMESTAMP
└─ operador_cierre_id BIGINT (FK)

Campos NUEVOS para auditoría (Rechazo):
├─ comentario_rechazo VARCHAR(1000)
├─ fecha_rechazo TIMESTAMP
└─ operador_rechazo_id BIGINT (FK)
```

### 2. ASIGNACION (Para evidencias del técnico)

```
Campos NUEVOS en ASIGNACION:
├─ comentario_resolucion VARCHAR(1000)
│  └─ Comentario del técnico al resolver
│
└─ fotos (Relación con tabla FOTOS)
   └─ Evidencias del trabajo realizado
   
Nota: ASIGNACION es el "trabajo en progreso"
      Almacena evidencias mientras técnico está trabajando
```

### 3. FOTO (NUEVA TABLA)

```
CREATE TABLE fotos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  asignacion_id BIGINT NOT NULL (FK),
  url VARCHAR(500),
  fecha_carga TIMESTAMP DEFAULT NOW(),
  tipo ENUM('INICIAL', 'PROCESO', 'FINAL'),
  FOREIGN KEY (asignacion_id) REFERENCES asignaciones(id)
);

O ALTERNATIVA: Guardar JSON en campo "fotos" del Reporte
```

---

## 🔍 Comparación: Antes vs Después

### TABLA REPORTE (ANTES)

```sql
CREATE TABLE reportes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(255) NOT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  estado ENUM('PENDIENTE','REVISION','PROCESO','RESUELTA','CERRADA','RECHAZADO'),
  prioridad ENUM('BAJA','MEDIA','ALTA'),
  cuenta_id BIGINT NOT NULL,
  ubicacion_id BIGINT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP DEFAULT NOW()
);
```

### TABLA REPORTE (DESPUÉS)

```sql
CREATE TABLE reportes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(255) NOT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  
  -- Estados
  estado ENUM('PENDIENTE','REVISION','PROCESO','RESUELTA','CERRADA',
              'RECHAZADO','RECHAZADO_AUDITO') DEFAULT 'PENDIENTE',
  prioridad ENUM('BAJA','MEDIA','ALTA'),
  
  -- Relaciones
  cuenta_id BIGINT NOT NULL,
  ubicacion_id BIGINT NOT NULL,
  
  -- Timestamps
  fecha_creacion TIMESTAMP DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP DEFAULT NOW(),
  
  -- ⭐ NUEVOS: Evidencias del Técnico
  comentario_resolucion VARCHAR(1000),
  
  -- ⭐ NUEVOS: Auditoría (Aprobación)
  comentario_cierre VARCHAR(1000),
  fecha_cierre TIMESTAMP,
  operador_cierre_id BIGINT,
  
  -- ⭐ NUEVOS: Auditoría (Rechazo)
  comentario_rechazo VARCHAR(1000),
  fecha_rechazo TIMESTAMP,
  operador_rechazo_id BIGINT,
  
  FOREIGN KEY (cuenta_id) REFERENCES cuentas(id),
  FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id),
  FOREIGN KEY (operador_cierre_id) REFERENCES cuentas(id),
  FOREIGN KEY (operador_rechazo_id) REFERENCES cuentas(id)
);

-- Índices
CREATE INDEX idx_reportes_estado ON reportes(estado);
CREATE INDEX idx_reportes_fecha_cierre ON reportes(fecha_cierre);
CREATE INDEX idx_reportes_fecha_rechazo ON reportes(fecha_rechazo);
```

### TABLA ASIGNACION (ANTES)

```sql
CREATE TABLE asignaciones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reporte_id BIGINT NOT NULL,
  operador_id BIGINT NOT NULL,
  tecnico_id BIGINT,
  fecha_asignacion TIMESTAMP DEFAULT NOW(),
  fecha_cierre TIMESTAMP
);
```

### TABLA ASIGNACION (DESPUÉS)

```sql
CREATE TABLE asignaciones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reporte_id BIGINT NOT NULL,
  operador_id BIGINT NOT NULL,
  tecnico_id BIGINT,
  fecha_asignacion TIMESTAMP DEFAULT NOW(),
  fecha_cierre TIMESTAMP,
  
  -- ⭐ NUEVOS: Comentario del Técnico
  comentario_resolucion VARCHAR(1000),
  
  FOREIGN KEY (reporte_id) REFERENCES reportes(id),
  FOREIGN KEY (operador_id) REFERENCES cuentas(id),
  FOREIGN KEY (tecnico_id) REFERENCES cuentas(id)
);
```

### TABLA FOTOS (NUEVA)

```sql
CREATE TABLE fotos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  asignacion_id BIGINT NOT NULL,
  reporte_id BIGINT NOT NULL,
  url VARCHAR(500) NOT NULL,
  tipo ENUM('INICIAL','PROCESO','FINAL') DEFAULT 'FINAL',
  fecha_carga TIMESTAMP DEFAULT NOW(),
  descripcion VARCHAR(500),
  
  FOREIGN KEY (asignacion_id) REFERENCES asignaciones(id) ON DELETE CASCADE,
  FOREIGN KEY (reporte_id) REFERENCES reportes(id) ON DELETE CASCADE
);

CREATE INDEX idx_fotos_asignacion ON fotos(asignacion_id);
CREATE INDEX idx_fotos_reporte ON fotos(reporte_id);
```

---

## 🎯 Escenarios de Uso

### ESCENARIO 1: Técnico Resuelve y Adjunta Evidencias   "Mis tareas y actualización de estado"

```
PASO 1: Técnico accede a reportes en PROCESO asignados a él
        GET /api/reportes?estado=PROCESO&tecnico_id={tecnicoId}
        ↓
        Respuesta: Solo reportes donde Técnico está asignado
        [
          {
            "id": 1,
            "titulo": "Bache Jr. Principal",
            "estado": "PROCESO",
            "asignacion": {
              "id": 5,
              "tecnico": { "id": 3, "nombre": "Luis Torres" }
            }
          }
        ]

PASO 2: Técnico realiza el trabajo
        ✅ Repara bache
        ✅ Cambia semáforo
        ✅ Poda árbol

PASO 3: Técnico adjunta FOTOS (OBLIGATORIO)
        POST /api/reportes/{id}/fotos
        Body: {
          "archivo": file,
          "tipo": "FINAL",
          "descripcion": "Trabajo completado"
        }
        ↓
        Validación:
        ✓ Mínimo 1 foto requerida
        ✓ Tipos válidos: INICIAL, PROCESO, FINAL
        ✓ Archivo no vacío
        ✓ Size < 10MB
        
        Respuesta 201:
        {
          "id": 123,
          "reporteId": 1,
          "url": "https://...fotos/123.jpg",
          "tipo": "FINAL",
          "fechaCarga": "2025-11-20T12:00:00"
        }

PASO 4: Técnico escribe comentario de resolución (OBLIGATORIO)
        PATCH /api/reportes/{id}/comentario-resolucion
        Body: {
          "comentarioResolucion": "Reparación completa, bache rellenado con asfalto nuevo"
        }
        ↓
        Validación:
        ✓ No puede ser vacío
        ✓ Max 1000 caracteres
        ✓ Mínimo 10 caracteres
        
        Se actualiza:
        • reporte.comentario_resolucion
        • asignacion.comentario_resolucion

PASO 5: Técnico marca como "Completado"
        PATCH /api/reportes/{id}/estado
        Body: { "estado": "RESUELTA" }
        ↓
        Validación previa:
        ✓ Tiene mínimo 1 foto ✓ (del PASO 3)
        ✓ Tiene comentario ✓ (del PASO 4)
        ✓ Estado actual es PROCESO
        
        Actualiza:
        • reporte.estado = RESUELTA
        • asignacion.fecha_cierre = NOW()
        
        ↓
        RESULT: Estado cambia a RESUELTA, listo para auditoría
```

### ESCENARIO 2: Operador Audita y Aprueba

```
PASO 1: Operador ve lista de reportes por estado (LISTA FILTRADA)
        GET /api/reportes?estado=RESUELTA
        ↓
        Respuesta: Todos los reportes en estado RESUELTA
        (sin importar qué operador los creó o audita)
        
        [
          {
            "id": 1,
            "titulo": "Bache Jr. Principal",
            "estado": "RESUELTA",
            "prioridad": "ALTA", 
            "ubicacion": { "latitud": -12.05, "longitud": -77.03 },
            "ciudadano": { "id": 1, "nombre": "Juan Pérez" },
            "tecnico": { "id": 3, "nombre": "Luis Torres" },
            "comentarioResolucion": "Reparación completa...",
            "fotos": [
              { "id": 10, "url": "...", "tipo": "FINAL" },
              { "id": 11, "url": "...", "tipo": "FINAL" }
            ],
            "fechaResuelto": "2025-11-20T12:00:00"
          },
          {
            "id": 2,
            "titulo": "Semáforo no funciona",
            "estado": "RESUELTA",
            ...
          },
          ...
        ]
        
        UX: En el Frontend se muestra una LISTA FILTRADA POR ESTADO:
        
        ┌─ Filtros ────────────────────────────────────────┐
        │ Estado: [PENDIENTE] [REVISION] [PROCESO]         │
        │         [RESUELTA] [CERRADA] [RECHAZADO]         │
        └──────────────────────────────────────────────────┘
        
        Reportes en RESUELTA:
        ┌─────────────────────────────────────────────────┐
        │ ID: 1 | Bache Jr. Principal | ALTA              │
        │ Técnico: Luis Torres | Fecha: 2025-11-20 12:00  │
        │ [VER DETALLES] [APROBAR] [RECHAZAR]             │
        ├─────────────────────────────────────────────────┤
        │ ID: 2 | Semáforo no funciona | MEDIA            │
        │ Técnico: Carlos López | Fecha: 2025-11-20 11:30 │
        │ [VER DETALLES] [APROBAR] [RECHAZAR]             │
        ├─────────────────────────────────────────────────┤
        │ ID: 3 | Árbol peligroso | BAJA                  │
        │ Técnico: María García | Fecha: 2025-11-20 10:15 │
        │ [VER DETALLES] [APROBAR] [RECHAZAR]             │
        └─────────────────────────────────────────────────┘

PASO 2: Operador hace clic en una fila o en [VER DETALLES]
        → Se abre modal o expande detalles del reporte
        ↓
        Se visualiza:
        ├─ Detalles del reporte
        ├─ Fotos adjuntas (thumbnails clickeables)
        ├─ Comentario del técnico
        ├─ Historial completo
        └─ 2 Botones de acción:
           ├─ ✅ "Aprobar Auditoría"
           └─ ❌ "Rechazar Auditoría"

PASO 3: Operador revisa fotos y comentario
        ✅ Hace clic en fotos para verlas en grande
        ✅ Lee comentario del técnico
        ✅ "Excelente trabajo, reparación visiblemente completa"

PASO 4: Operador APRUEBA (Botón Verde)
        POST /api/reportes/{id}/cerrar
        Body: {
          "reporteId": 1,
          "operadorId": 5,
          "comentarioCierre": "Auditoría OK, trabajo validado"
        }
        ↓
        Sistema valida:
        • Estado es RESUELTA ✓
        • Tiene fotos ✓ (Fueron obligatorias)
        • Tiene comentario ✓ (Fue obligatorio)
        • Operador existe ✓
        
        Sistema actualiza:
        • estado = CERRADA
        • comentario_cierre = "Auditoría OK..."
        • fecha_cierre = NOW()
        • operador_cierre_id = 5
        
        Respuesta 200:
        {
          "id": 1,
          "estado": "CERRADA",
          "comentarioCierre": "Auditoría OK...",
          "fechaCierre": "2025-11-20T14:30:00",
          "operadorCierre": { "id": 5, "nombre": "Ana García" }
        }
        
        ↓
        ENVIADA NOTIFICACIÓN AL CIUDADANO
        "✅ Tu reporte ha sido resuelto y auditado correctamente"
        
        ↓
        El combobox se actualiza automáticamente:
        [El reporte desaparece de la lista - Ya no está en RESUELTA]

RESULTADO:
┌────────────────────────────────────────────────────────────┐
│ Reporte 1: Bache Jr. Principal                             │
├────────────────────────────────────────────────────────────┤
│ Estado: CERRADA ✅                                          │
│ Creado: 2025-11-19 10:30 (Juan Pérez)                      │
│ Asignado: 2025-11-19 15:45 → Luis Torres (Técnico)        │
│ Resuelto: 2025-11-20 12:00 (Luis Torres)                  │
│ ├─ Comentario: "Reparación completa..."                   │
│ ├─ Fotos: 3 adjuntas ✅ (OBLIGATORIAS)                     │
│ Auditado: 2025-11-20 14:30 (Ana García - Operador)        │
│ └─ Comentario: "Auditoría OK, trabajo validado"           │
└────────────────────────────────────────────────────────────┘
```

### ESCENARIO 3: Operador Rechaza Auditoría

```
PASO 1: Operador accede a "Control de Calidad"
        GET /api/reportes?estado=RESUELTA

PASO 2: Selecciona reporte
        GET /api/reportes/{id}

PASO 3: Operador revisa fotos
        ❌ "Las fotos no muestran reparación completa"
        ❌ "El trabajo es insuficiente"

PASO 4: Operador RECHAZA (Botón Rojo)
        POST /api/reportes/{id}/rechazar-audito
        Body: {
          "reporteId": 1,
          "operadorId": 5,
          "comentarioRechazo": "Fotos insuficientes. El bache no está completamente relleno. Requiere más trabajo"
        }
        ↓
        Validación:
        ✓ Estado es RESUELTA
        ✓ Operador existe
        ✓ comentarioRechazo es OBLIGATORIO (no puede estar vacío)
        ✓ Max 1000 caracteres
        ✓ contador_rechazos < MAX_RECHAZOS_AUDITO (3)
        
        Sistema actualiza:
        • estado = RECHAZADO_AUDITO
        • comentario_rechazo = "Fotos insuficientes..."
        • fecha_rechazo = NOW()
        • operador_rechazo_id = 5
        • contador_rechazos_audito = 1
        
        Respuesta 200:
        {
          "id": 1,
          "estado": "RECHAZADO_AUDITO",
          "comentarioRechazo": "Fotos insuficientes...",
          "fechaRechazo": "2025-11-20T14:35:00",
          "operadorRechazo": { "id": 5, "nombre": "Ana García" },
          "contadorRechazos": 1,
          "maxRechazosPermitidos": 3
        }
        
        ↓
        El combobox se actualiza automáticamente:
        [El reporte desaparece - Ya no está en RESUELTA]
        
        ↓
        ENVIADA NOTIFICACIÓN AL TÉCNICO
        "⚠️ Tu reporte fue rechazado en auditoría"
        "Motivo: Fotos insuficientes. El bache..."
        "Puedes reintentar el trabajo"

PASO 5: Técnico recibe notificación
        ⚠️ "Auditoría rechazada: Fotos insuficientes..."
        ↓
        Accede a reporte con filtro: GET /api/reportes?estado=RECHAZADO_AUDITO&tecnico_id={tecnicoId}
        ↓
        Ve:
        ├─ Estado: RECHAZADO_AUDITO
        ├─ Comentario del operador: "Fotos insuficientes..."
        ├─ Sus fotos anteriores (para referencia)
        ├─ Botón: "Reintentar (Pasos 1-5)"
        └─ Contador: "Intento 1 de 3"

PASO 6: Técnico regresa a trabajar
        PATCH /api/reportes/{id}/estado
        Body: { "estado": "PROCESO" }
        ↓
        Validación:
        ✓ Estado actual es RECHAZADO_AUDITO
        ✓ Nuevo estado es PROCESO
        
        Sistema:
        • estado = PROCESO (regresa a trabajo)
        • asignacion queda activa
        • Técnico puede adjuntar nuevas fotos
        
        ↓
        NOTIFICACIÓN AL OPERADOR
        "Técnico ha retomado el reporte para hacer correcciones"

PASO 7: Técnico adjunta NUEVAS FOTOS
        POST /api/reportes/{id}/fotos
        (Repite pasos de adjuntar evidencias)
        ↓
        Validación:
        ✓ Estado es PROCESO
        ✓ Mínimo 1 foto
        ✓ Size < 10MB

PASO 8: Técnico escribe NUEVO comentario
        PATCH /api/reportes/{id}/comentario-resolucion
        Body: {
          "comentarioResolucion": "Segunda reparación, bache completamente relleno y compactado correctamente"
        }
        ↓
        Validación:
        ✓ No vacío
        ✓ Max 1000 caracteres

PASO 9: Técnico marca completado nuevamente
        PATCH /api/reportes/{id}/estado
        Body: { "estado": "RESUELTA" }
        ↓
        Validación:
        ✓ Tiene nuevas fotos
        ✓ Tiene comentario actualizado
        
        • estado = RESUELTA (nuevamente para auditoría)

PASO 10: Operador audita de nuevo
        GET /api/reportes?estado=RESUELTA
        ↓
        El reporte reaparece en el combobox
        (En el historial se ve "Intento 2 de 3")
        
        Operador selecciona y ve:
        ├─ Las NUEVAS fotos del técnico
        ├─ Nuevo comentario
        ├─ Contador: "Auditoría - Intento 2 de 3"
        └─ 2 opciones:
           ├─ ✅ "Aprobar (CERRADA)"
           └─ ❌ "Rechazar nuevamente (RECHAZADO_AUDITO)"

PASO 11: Operador APRUEBA (Segunda Vez)
        POST /api/reportes/{id}/cerrar
        Body: {
          "reporteId": 1,
          "operadorId": 5,
          "comentarioCierre": "Segunda auditoría OK, correcciones validadas"
        }
        ↓
        • estado = CERRADA ✅
        • comentario_cierre = "Segunda auditoría OK..."
        • fecha_cierre = NOW()
        
        ↓
        NOTIFICACIÓN AL CIUDADANO
        "✅ Tu reporte fue finalmente resuelto después de correcciones"

RESULTADO (CASO DE RECHAZO Y REINTENTOS):
┌────────────────────────────────────────────────────────────┐
│ Reporte 1: Bache Jr. Principal                             │
├────────────────────────────────────────────────────────────┤
│ Estado: CERRADA ✅ (Después de reintentos)                 │
│ Creado: 2025-11-19 10:30 (Juan Pérez)                      │
│ Asignado: 2025-11-19 15:45 → Luis Torres (Técnico)        │
│                                                            │
│ Intento 1:                                                 │
│ ├─ Resuelto: 2025-11-20 12:00                              │
│ ├─ Fotos: 3 adjuntas                                       │
│ ├─ Auditoría rechazada: 2025-11-20 14:35                   │
│ └─ Motivo: "Fotos insuficientes..."                        │
│                                                            │
│ Intento 2:                                                 │
│ ├─ Retomado: 2025-11-20 15:00                              │
│ ├─ Nuevas fotos: 4 adjuntas                                │
│ ├─ Resuelto: 2025-11-20 16:30                              │
│ ├─ Auditoría aprobada: 2025-11-20 17:00                    │
│ └─ Operador: Ana García                                    │
└────────────────────────────────────────────────────────────┘

NOTA: Si se alcanza MAX_RECHAZOS (3), el operador tiene opción
      POST /api/reportes/{id}/rechazar-definitivo
      → estado = RECHAZADO (FINAL, sin reintentos)
```

---

## 🌐 API Endpoints Nuevos

### 1. Técnico Adjunta Fotos (OBLIGATORIO)

```http
POST /api/reportes/{id}/fotos
Content-Type: multipart/form-data

Form Data:
├─ archivo (file, REQUIRED)
├─ tipo (enum: INICIAL, PROCESO, FINAL) REQUIRED
└─ descripcion (string, OPTIONAL)

Respuesta 201:
{
  "id": 123,
  "reporteId": 1,
  "url": "https://...fotos/123.jpg",
  "tipo": "FINAL",
  "fechaCarga": "2025-11-20T12:00:00"
}

Validaciones OBLIGATORIAS (para ESCENARIO 1):
✓ Reporte existe
✓ Estado es PROCESO
✓ Técnico es el asignado
✓ Archivo es imagen válida
✓ Tamaño < 10MB
✓ Mínimo 1 foto ANTES de marcar RESUELTA
✓ Tipo debe ser uno de: INICIAL, PROCESO, FINAL

Errores Posibles:
❌ 404 Reporte no encontrado
❌ 400 Estado no es PROCESO
❌ 403 Técnico no es el asignado
❌ 400 Archivo inválido o vacío
❌ 413 Archivo > 10MB
```

### 2. Técnico Escribe Comentario de Resolución (OBLIGATORIO)

```http
PATCH /api/reportes/{id}/comentario-resolucion
Content-Type: application/json

Body:
{
  "comentarioResolucion": "Reparación completa, bache rellenado con asfalto nuevo, compactado"
}

Respuesta 200:
{
  "id": 1,
  "titulo": "Bache Jr. Principal",
  "estado": "PROCESO",
  "comentarioResolucion": "Reparación completa...",
  "fotos": [
    { "id": 10, "url": "...", "tipo": "FINAL" },
    { "id": 11, "url": "...", "tipo": "FINAL" }
  ]
}

Validaciones OBLIGATORIAS (para ESCENARIO 1):
✓ Reporte existe
✓ Estado es PROCESO
✓ Técnico es el asignado
✓ Comentario NO PUEDE ser vacío
✓ Mínimo 10 caracteres
✓ Max 1000 caracteres
✓ Mínimo 1 foto ANTES de cambiar a RESUELTA (del endpoint 1)

Errores Posibles:
❌ 404 Reporte no encontrado
❌ 400 Estado no es PROCESO
❌ 403 Técnico no es el asignado
❌ 400 Comentario vacío
❌ 400 Comentario < 10 caracteres
❌ 400 Comentario > 1000 caracteres
```

### 3. Técnico Marca como Completado (Estado a RESUELTA) - VALIDACIÓN FINAL

```http
PATCH /api/reportes/{id}/estado
Content-Type: application/json

Body:
{
  "estado": "RESUELTA",
  "motivo": "Trabajo completado con evidencias"
}

Respuesta 200:
{
  "id": 1,
  "estado": "RESUELTA",
  "comentarioResolucion": "Reparación completa...",
  "fotos": [
    { "id": 10, "url": "...", "tipo": "FINAL" },
    { "id": 11, "url": "...", "tipo": "FINAL" }
  ],
  "asignacion": {
    "id": 5,
    "tecnico": { "id": 3, "nombre": "Luis Torres" },
    "estado": "ACTIVA"
  }
}

Validaciones CRÍTICAS (para ESCENARIO 1):
✓ Reporte existe
✓ Estado ACTUAL es PROCESO
✓ Nuevo estado es RESUELTA
✓ TIENE MÍNIMO 1 FOTO (del endpoint 1) ← OBLIGATORIO
✓ TIENE COMENTARIO NO VACÍO (del endpoint 2) ← OBLIGATORIO
✓ Técnico es el asignado

Errores Posibles:
❌ 404 Reporte no encontrado
❌ 400 Estado actual no es PROCESO
❌ 400 SIN FOTOS ADJUNTAS (no puede cambiar a RESUELTA sin fotos)
❌ 400 SIN COMENTARIO DE RESOLUCIÓN (no puede cambiar sin comentario)
❌ 403 Técnico no es el asignado

NOTA IMPORTANTE:
→ Si el técnico intenta cambiar a RESUELTA sin fotos o sin comentario,
  recibe error 400 explicando qué le falta.
→ Debe completar AMBOS antes de poder cambiar de estado.
```

### 4. Operador Audita y Aprueba

```http
POST /api/reportes/{id}/cerrar
Content-Type: application/json

Body:
{
  "reporteId": 1,
  "operadorId": 5,
  "comentarioCierre": "Auditoría OK, trabajo validado, fotos claras"
}

Respuesta 200:
{
  "id": 1,
  "estado": "CERRADA",
  "comentarioCierre": "Auditoría OK...",
  "fechaCierre": "2025-11-20T14:30:00",
  "operadorCierre": { "id": 5, "nombre": "Ana García" },
  "auditoriaCompleta": {
    "creado": "2025-11-19T10:30:00",
    "asignado": "2025-11-19T15:45:00",
    "resuelto": "2025-11-20T12:00:00",
    "auditado": "2025-11-20T14:30:00"
  }
}

Validaciones (Operador GLOBAL puede auditar cualquier reporte):
✓ Reporte existe
✓ Estado es RESUELTA
✓ Operador es OPERADOR_MUNICIPAL
✓ Operador existe
✓ Reporte TIENE FOTOS (fueron obligatorias)
✓ Reporte TIENE COMENTARIO (fue obligatorio)
✓ Comentario cierre es OPCIONAL (pero recomendado)

Errores Posibles:
❌ 404 Reporte no encontrado
❌ 400 Estado no es RESUELTA
❌ 403 Usuario no es operador municipal
❌ 400 Reporte sin fotos adjuntas
❌ 400 Reporte sin comentario de técnico

NOTA:
→ El operador que audita puede ser CUALQUIER operador municipal
→ No está limitado a los que crearon el reporte
```

### 5. Operador Rechaza Auditoría (OBLIGATORIO comentario, GLOBAL sin restricciones)

```http
POST /api/reportes/{id}/rechazar-audito
Content-Type: application/json

Body:
{
  "reporteId": 1,
  "operadorId": 5,
  "comentarioRechazo": "Fotos insuficientes. El bache no está completamente relleno. Requiere más trabajo"
}

Respuesta 200:
{
  "id": 1,
  "estado": "RECHAZADO_AUDITO",
  "comentarioRechazo": "Fotos insuficientes...",
  "fechaRechazo": "2025-11-20T14:35:00",
  "operadorRechazo": { "id": 5, "nombre": "Ana García" },
  "tecnico": { "id": 3, "nombre": "Luis Torres" },
  "contadorRechazos": 1,
  "maxRechazosPermitidos": 3
}

Validaciones (Operador GLOBAL puede rechazar cualquier reporte):
✓ Reporte existe
✓ Estado es RESUELTA
✓ Operador es OPERADOR_MUNICIPAL
✓ comentarioRechazo es OBLIGATORIO (no puede estar vacío)
✓ Mínimo 10 caracteres
✓ Max 1000 caracteres
✓ contador_rechazos < MAX_RECHAZOS_AUDITO (3)

Errores Posibles:
❌ 404 Reporte no encontrado
❌ 400 Estado no es RESUELTA
❌ 403 Usuario no es operador municipal
❌ 400 Comentario rechazo vacío (OBLIGATORIO)
❌ 400 Comentario < 10 caracteres
❌ 400 Comentario > 1000 caracteres
❌ 400 Se alcanzó máximo de rechazos (usar rechazar-definitivo)

NOTA:
→ El operador que rechaza puede ser CUALQUIER operador municipal
→ El técnico recibirá notificación del rechazo
→ El técnico puede reintentar el trabajo
→ El comentario OBLIGATORIO explica por qué se rechaza
```

### 6. Operador Rechaza Definitivamente (NUEVO)

```http
POST /api/reportes/{id}/rechazar-definitivo
Content-Type: application/json

Body:
{
  "reporteId": 1,
  "operadorId": 5,
  "motivo": "Se cancela reporte. Ciudadano no proporcionó acceso a ubicación"
}

Respuesta 200:
{
  "id": 1,
  "estado": "RECHAZADO",
  "motivoRechazo": "Se cancela reporte...",
  "fechaRechazo": "2025-11-20T14:35:00",
  "operadorRechazo": { "id": 5, "nombre": "Ana García" },
  "notificacionEnviada": true
}

Validaciones:
✓ Reporte existe
✓ Estado es RECHAZADO_AUDITO (después de rechazos múltiples)
✓ Motivo obligatorio
```

### 7. Operador ve Reportes para Auditar (LISTA FILTRADA POR ESTADO)

```http
GET /api/reportes?estado=RESUELTA
Authorization: Bearer token

Respuesta 200:
[
  {
    "id": 1,
    "titulo": "Bache Jr. Principal",
    "estado": "RESUELTA",
    "prioridad": "ALTA",
    "ubicacion": { "latitud": -12.05, "longitud": -77.03, "direccion": "Jr. Principal 123" },
    "ciudadano": { "id": 1, "nombre": "Juan Pérez" },
    "tecnico": { "id": 3, "nombre": "Luis Torres" },
    "comentarioResolucion": "Reparación completa...",
    "fotos": [
      { "id": 10, "url": "...", "tipo": "FINAL", "fechaCarga": "2025-11-20T12:00:00" },
      { "id": 11, "url": "...", "tipo": "FINAL", "fechaCarga": "2025-11-20T12:05:00" }
    ],
    "fechaResuelto": "2025-11-20T12:00:00",
    "contadorRechazos": 0
  },
  ...
]

UX: LISTA FILTRADA POR ESTADO (sin ventana nueva):

┌─ Filtros ────────────────────────────────────────────┐
│ Estado: [PENDIENTE] [REVISION] [PROCESO]             │
│         [RESUELTA] [CERRADA] [RECHAZADO]             │
└──────────────────────────────────────────────────────┘

Reportes en RESUELTA (para auditoría):
┌────────────────────────────────────────────────────────────────┐
│ ID: 1 | Bache Jr. Principal | ALTA | Luis Torres             │
│ Fecha: 2025-11-20 12:00 | Fotos: 3 | Comentario: ✓           │
│ [VER DETALLES] [APROBAR] [RECHAZAR]                          │
├────────────────────────────────────────────────────────────────┤
│ ID: 2 | Semáforo no funciona | MEDIA | Carlos López          │
│ Fecha: 2025-11-20 11:30 | Fotos: 2 | Comentario: ✓           │
│ [VER DETALLES] [APROBAR] [RECHAZAR]                          │
├────────────────────────────────────────────────────────────────┤
│ ID: 3 | Árbol peligroso | BAJA | María García                │
│ Fecha: 2025-11-20 10:15 | Fotos: 1 | Comentario: ✓           │
│ [VER DETALLES] [APROBAR] [RECHAZAR]                          │
└────────────────────────────────────────────────────────────────┘

Al hacer clic en [VER DETALLES]:
├─ Se abre MODAL o expande fila
├─ Se muestran fotos (thumbnails clickeables)
├─ Se muestra comentario completo del técnico
├─ Se muestran detalles del reporte
└─ Se activan botones: ✅ Aprobar | ❌ Rechazar

Al hacer clic en [APROBAR]:
├─ POST /api/reportes/{id}/cerrar
└─ Fila desaparece de la lista (estado → CERRADA)

Al hacer clic en [RECHAZAR]:
├─ POST /api/reportes/{id}/rechazar-audito
└─ Fila desaparece de lista RESUELTA (estado → RECHAZADO_AUDITO)

Filtros disponibles por estado:
- PENDIENTE: Reportes que acaban de crearse
- REVISION: Operador revisa antes de asignar
- PROCESO: Técnico está trabajando
- RESUELTA: **Esperando auditoría** ← Para operador
- CERRADA: Reportes auditados y aprobados
- RECHAZADO: Reportes rechazados definitivamente

NOTA:
→ El operador ve TODOS los reportes en RESUELTA
→ Sin importar qué operador los creó
→ Sin filtro de operador_id
→ Cualquier operador municipal puede auditar cualquier reporte
→ La interfaz es una LISTA simple filtrada por estado
```

---

## 📊 Matriz de Estados y Transiciones

```
┌─────────────┬───────────────────────────────────────────────┐
│ Estado      │ Transiciones Permitidas                        │
├─────────────┼───────────────────────────────────────────────┤
│ PENDIENTE   │ → REVISION (Ciudadano)                        │
│             │ → RECHAZADO (Admin)                           │
├─────────────┼───────────────────────────────────────────────┤
│ REVISION    │ → PROCESO (Operador asigna)                  │
│             │ → RECHAZADO (Operador rechaza)               │
├─────────────┼───────────────────────────────────────────────┤
│ PROCESO     │ → RESUELTA (Técnico completa)                │
│             │ → RECHAZADO (Operador rechaza)               │
├─────────────┼───────────────────────────────────────────────┤
│ RESUELTA    │ → CERRADA (Operador aprueba)  ✅             │
│             │ → RECHAZADO_AUDITO (Operador rechaza) ❌ (NEW)│
│             │ → RECHAZADO (Rechazo definitivo)             │
├─────────────┼───────────────────────────────────────────────┤
│ RECHAZADO   │ [ESTADO FINAL - No transiciona]              │
│   _AUDITO   │ → PROCESO (Técnico reinicia trabajo)         │
│             │ → RECHAZADO (Rechazo definitivo)             │
├─────────────┼───────────────────────────────────────────────┤
│ CERRADA     │ [ESTADO FINAL - No transiciona]              │
├─────────────┼───────────────────────────────────────────────┤
│ RECHAZADO   │ [ESTADO FINAL - No transiciona]              │
└─────────────┴───────────────────────────────────────────────┘
```

---

## ⚡ Modelo Actualizado

```java
public enum EstadoReporte {
    PENDIENTE,              // 1. Inicial
    REVISION,               // 2. Operador revisa
    PROCESO,                // 3. Técnico trabaja
    RESUELTA,               // 4. Técnico completó + evidencias
    CERRADA,                // 5. Operador aprobó (FINAL)
    RECHAZADO_AUDITO,       // 5b. Operador rechazó (Técnico puede reintentar)
    RECHAZADO               // 5c. Rechazo definitivo (FINAL)
}
```

---

## 📱 Flujo UX por Rol

### CIUDADANO

```
1. Crea reporte (foto inicial, ubicación, descripción)
2. Ve estado en dashboard
3. Recibe notificaciones:
   ├─ "Se asignó técnico"
   ├─ "Técnico está trabajando"
   ├─ "✅ Reporte resuelto" (cuando estado → CERRADA)
   ├─ "⚠️ Técnico está reintentando" (si estado → RECHAZADO_AUDITO → PROCESO)
   └─ "❌ Se canceló reporte" (si estado → RECHAZADO)
4. Ve fotos finales cuando CERRADA
```

### TÉCNICO

```
1. VE SOLO SUS REPORTES ASIGNADOS
   GET /api/reportes?estado=PROCESO&tecnico_id={tecnicoId}
   ├─ Ve lista de reportes asignados a él
   └─ Filtrados por técnico

2. Para cada reporte:
   ├─ Realiza trabajo
   ├─ Adjunta FOTOS (OBLIGATORIO)
   ├─ Escribe COMENTARIO (OBLIGATORIO)
   └─ Marca como RESUELTA (valida fotos + comentario)

3. Espera auditoría

4. Si RECHAZADO_AUDITO:
   ├─ Ve comentario del operador (por qué fue rechazado)
   ├─ Puede ver sus fotos anteriores
   ├─ Reingresa a PROCESO
   ├─ Adjunta NUEVAS fotos
   ├─ Escribe NUEVO comentario
   └─ Marca completado nuevamente

5. Si CERRADA: ✅ "Trabajo validado"
6. Si RECHAZADO: ❌ "Reporte cancelado"
```

### OPERADOR

```
1. Ve LISTA de reportes filtrados por estado
   GET /api/reportes?estado=RESUELTA
   ├─ Ve TODOS los reportes en RESUELTA
   ├─ Sin importar quién los creó
   ├─ LISTA con filtros por estado
   └─ Filtrados por prioridad, fecha, etc.

2. Interfaz muestra botones de filtro:
   [PENDIENTE] [REVISION] [PROCESO] [RESUELTA] [CERRADA] [RECHAZADO]
   └─ Hace clic en RESUELTA para ver reportes pendientes de auditoría

3. Ve detalles:
   ├─ Todas las fotos del técnico (thumbnails clickeables)
   ├─ Comentario del técnico
   ├─ Detalles del reporte
   └─ Historial completo

4. OPCIÓN A: ✅ Aprueba (Botón Verde)
   ├─ POST /api/reportes/{id}/cerrar
   ├─ Escribe comentario (opcional)
   └─ Estado → CERRADA (FINAL)

5. OPCIÓN B: ❌ Rechaza (Botón Rojo - con reintentos)
   ├─ POST /api/reportes/{id}/rechazar-audito
   ├─ Escribe MOTIVO OBLIGATORIO
   └─ Estado → RECHAZADO_AUDITO (Técnico puede reintentar)

6. Si se alcanza MAX_RECHAZOS (3):
   ├─ OPCIÓN C: Rechaza definitivo
   ├─ POST /api/reportes/{id}/rechazar-definitivo
   ├─ Escribe MOTIVO
   └─ Estado → RECHAZADO (FINAL)

7. La LISTA se actualiza automáticamente
   └─ Reportes auditados desaparecen de la lista
```

---

## 🔧 Configuración y Políticas

```properties
# application.properties

# FOTO de resolución es OBLIGATORIA para cambiar a RESUELTA
reporte.resolucion.foto.obligatoria=true

# COMENTARIO de resolución es OBLIGATORIO para cambiar a RESUELTA
reporte.resolucion.comentario.obligatorio=true

# COMENTARIO de rechazo es OBLIGATORIO cuando operador rechaza
reporte.rechazo.comentario.obligatorio=true

# Mínimo caracteres para comentarios
reporte.comentario.minimo.caracteres=10

# Máximo caracteres para comentarios
reporte.comentario.maximo.caracteres=1000

# Máximo rechazos antes de convertir a RECHAZADO definitivo
reporte.max.rechazos.audito=3

# Tamaño máximo de foto
reporte.foto.max.size=10485760

# Tipos de foto permitidos
reporte.foto.tipos.permitidos=INICIAL,PROCESO,FINAL

# FILTROS POR TÉCNICO: Técnico ve SOLO sus reportes asignados
reporte.tecnico.filtro.asignados=true

# FILTROS POR OPERADOR: Operador ve TODOS los reportes en RESUELTA
reporte.operador.filtro.globales=true
```

---

## ✅ Validaciones por Endpoint

### 1. GET /api/reportes?estado=PROCESO&tecnico_id={tecnicoId} (Técnico ve SUS reportes)

```
Validaciones:
✓ Usuario es TÉCNICO
✓ tecnico_id coincide con usuario autenticado
✓ Solo trae reportes asignados a ESTE técnico
✓ Solo trae reportes en estado PROCESO

Resultado:
✓ Técnico VE SOLO sus reportes asignados
✗ Técnico NO VE reportes de otros técnicos
✗ Técnico NO VE reportes en otros estados
```

### 2. POST /api/reportes/{id}/fotos (Técnico adjunta FOTOS - OBLIGATORIO)

```
Validaciones:
✓ Reporte existe
✓ Estado es PROCESO
✓ Técnico es el asignado a este reporte
✓ Archivo es imagen válida
✓ Tamaño < 10MB
✓ Tipo es uno de: INICIAL, PROCESO, FINAL
✓ Mínimo 1 foto ANTES de cambiar a RESUELTA

Errores:
❌ 404 Reporte no encontrado
❌ 400 Estado no es PROCESO
❌ 403 Técnico no es el asignado
❌ 400 Archivo inválido
❌ 413 Archivo > 10MB
❌ 400 Tipo de foto inválido
```

### 3. PATCH /api/reportes/{id}/comentario-resolucion (Técnico escribe COMENTARIO - OBLIGATORIO)

```
Validaciones:
✓ Reporte existe
✓ Estado es PROCESO
✓ Técnico es el asignado
✓ Comentario NO vacío
✓ Mínimo 10 caracteres
✓ Max 1000 caracteres
✓ Mínimo 1 foto ANTES (del endpoint anterior)

Errores:
❌ 404 Reporte no encontrado
❌ 400 Estado no es PROCESO
❌ 403 Técnico no es el asignado
❌ 400 Comentario vacío
❌ 400 Comentario < 10 caracteres
❌ 400 Comentario > 1000 caracteres
```

### 4. PATCH /api/reportes/{id}/estado (Técnico cambia a RESUELTA - VALIDACIÓN FINAL)

```
Validaciones CRÍTICAS para ESCENARIO 1:
✓ Reporte existe
✓ Estado ACTUAL es PROCESO
✓ Nuevo estado es RESUELTA
✓ TIENE MÍNIMO 1 FOTO (del endpoint 2) ← OBLIGATORIO
✓ TIENE COMENTARIO (del endpoint 3) ← OBLIGATORIO
✓ Técnico es el asignado

Errores:
❌ 404 Reporte no encontrado
❌ 400 Estado actual no es PROCESO
❌ 400 SIN FOTOS (no puede cambiar sin fotos)
❌ 400 SIN COMENTARIO (no puede cambiar sin comentario)
❌ 403 Técnico no es el asignado
```

### 5. GET /api/reportes?estado=RESUELTA (Operador VE TODOS - GLOBAL)

```
Validaciones:
✓ Usuario es OPERADOR_MUNICIPAL
✓ Trae TODOS los reportes en RESUELTA
✓ SIN filtro de operador_id (NO restringe por quién los creó)
✓ Operador VE reportes de TODOS los operadores

Resultado:
✓ Operador MUNICIPAL VE todos los reportes pendientes auditoría
✗ No importa quién creó el reporte
✗ No importa qué operador los asignó

COMBOBOX FRONTEND:
┌─ Reportes para Auditoría ──────────────────────────────┐
│ ⏬ Selecciona reporte:                                 │
│   [ID: 1 - Bache Jr. Principal (ALTA)]                │
│   [ID: 2 - Semáforo no funciona (MEDIA)]              │
│   [ID: 3 - Árbol peligroso (BAJA)]                    │
└────────────────────────────────────────────────────────┘
```

### 6. POST /api/reportes/{id}/cerrar (Operador APRUEBA - GLOBAL)

```
Validaciones:
✓ Reporte existe
✓ Estado es RESUELTA
✓ Operador es OPERADOR_MUNICIPAL
✓ Operador existe
✓ Reporte TIENE fotos (fueron obligatorias)
✓ Reporte TIENE comentario (fue obligatorio)
✓ Operador PUEDE SER CUALQUIERA (sin restricción)

Errores:
❌ 404 Reporte no encontrado
❌ 400 Estado no es RESUELTA
❌ 403 Usuario no es operador municipal
❌ 400 Reporte sin fotos
❌ 400 Reporte sin comentario

NOTA: Cualquier operador municipal puede auditar cualquier reporte
```

### 7. POST /api/reportes/{id}/rechazar-audito (Operador RECHAZA - GLOBAL)

```
Validaciones:
✓ Reporte existe
✓ Estado es RESUELTA
✓ Operador es OPERADOR_MUNICIPAL
✓ Operador PUEDE SER CUALQUIERA (sin restricción)
✓ comentarioRechazo es OBLIGATORIO (no vacío)
✓ Mínimo 10 caracteres
✓ Max 1000 caracteres
✓ contador_rechazos < MAX_RECHAZOS_AUDITO (3)

Errores:
❌ 404 Reporte no encontrado
❌ 400 Estado no es RESUELTA
❌ 403 Usuario no es operador municipal
❌ 400 Comentario rechazo vacío (OBLIGATORIO)
❌ 400 Comentario < 10 caracteres
❌ 400 Comentario > 1000 caracteres
❌ 400 Se alcanzó máximo de rechazos

NOTA: Operador GLOBAL puede rechazar cualquier reporte
```

---

## 📊 Diagrama: Tabla FOTOS

```
FOTOS (NUEVA TABLA)
┌─────────────────────────────────────────────────────────┐
│ Idea: Almacenar evidencias del trabajo del técnico      │
└─────────────────────────────────────────────────────────┘

Campos:
├─ id (BIGINT, PK, AUTO_INCREMENT)
├─ asignacion_id (BIGINT, FK → asignaciones)
├─ reporte_id (BIGINT, FK → reportes)
├─ url (VARCHAR 500)
│  └─ Ruta/URL donde se almacenan
│     Ej: https://storage.example.com/fotos/123/foto1.jpg
├─ tipo (ENUM: INICIAL, PROCESO, FINAL)
│  └─ INICIAL: Foto del problema al llegar
│  └─ PROCESO: Fotos durante reparación
│  └─ FINAL: Foto del trabajo completado
├─ fecha_carga (TIMESTAMP DEFAULT NOW())
├─ descripcion (VARCHAR 500, OPTIONAL)
│  └─ Descripción de la foto
│  └─ Ej: "Vista del bache reparado"
└─ Índices:
   ├─ PK: id
   ├─ FK: asignacion_id
   ├─ FK: reporte_id
   └─ Composite: (asignacion_id, tipo)

Relaciones:
├─ 1 ASIGNACION ←→ MUCHAS FOTOS
├─ 1 REPORTE ←→ MUCHAS FOTOS
└─ FOTOS.asignacion_id = ASIGNACIONES.id
└─ FOTOS.reporte_id = REPORTES.id

Ejemplos de Datos:
┌────┬─────────────┬──────────────┬───────────────────────┬────────┬──────────────┬──────────────────────────────┐
│ id │ asignacion  │ reporte_id   │ url                   │ tipo   │ fecha_carga  │ descripcion                  │
├────┼─────────────┼──────────────┼───────────────────────┼────────┼──────────────┼──────────────────────────────┤
│ 10 │ 5           │ 1            │ https://.../1.jpg     │ FINAL  │ 2025-11-20   │ Bache reparado               │
│ 11 │ 5           │ 1            │ https://.../2.jpg     │ FINAL  │ 2025-11-20   │ Vista lateral                │
│ 12 │ 5           │ 1            │ https://.../3.jpg     │ FINAL  │ 2025-11-20   │ Detalle de la reparación     │
└────┴─────────────┴──────────────┴───────────────────────┴────────┴──────────────┴──────────────────────────────┘
```

---

## 📋 Checklist de Implementación

```
PARTE 1: Actualizar Modelos
─────────────────────────────
☐ EstadoReporte.java
  └─ Agregar RECHAZADO_AUDITO

☐ Reporte.java
  ├─ Agregar: comentario_resolucion
  ├─ Agregar: comentario_cierre
  ├─ Agregar: fecha_cierre
  ├─ Agregar: operador_cierre_id
  ├─ Agregar: comentario_rechazo
  ├─ Agregar: fecha_rechazo
  ├─ Agregar: operador_rechazo_id
  ├─ Relación OneToMany → Fotos
  └─ Métodos: cerrarReporte(), rechazarAudito(), rechazarDefinitivo()

☐ Asignacion.java
  ├─ Agregar: comentario_resolucion
  └─ Relación OneToMany → Fotos

☐ Foto.java (NUEVA)
  ├─ id, asignacion_id, reporte_id
  ├─ url, tipo (ENUM)
  ├─ fecha_carga, descripcion
  └─ Getters/Setters

PARTE 2: DTOs
─────────────
☐ FotoDTO.java (NUEVA)
☐ ReporteDTO.java (ACTUALIZADA)
  ├─ comentario_resolucion
  ├─ fotos (List<FotoDTO>)
  ├─ comentario_cierre
  ├─ fecha_cierre
  ├─ operador_cierre
  ├─ comentario_rechazo
  ├─ fecha_rechazo
  └─ operador_rechazo

PARTE 3: Servicios
──────────────────
☐ ICierreReporteService.java
  ├─ cerrarReporte()
  ├─ rechazarAudito()
  └─ rechazarDefinitivo()

☐ CierreReporteServiceImpl.java
  ├─ Todas las validaciones
  ├─ Actualizar estado
  ├─ Guardar comentarios y fechas
  ├─ Enviar notificaciones
  └─ @Transactional

☐ IFotoService.java (NUEVA)

☐ FotoServiceImpl.java (NUEVA)
  ├─ subirFoto()
  ├─ obtenerFotosPorReporte()
  └─ validar tipo y tamaño

PARTE 4: Controllers
───────────────────
☐ CierreReporteController.java
  ├─ POST /api/reportes/{id}/cerrar
  ├─ POST /api/reportes/{id}/rechazar-audito
  └─ POST /api/reportes/{id}/rechazar-definitivo

☐ FotoController.java (NUEVA)
  ├─ POST /api/reportes/{id}/fotos
  ├─ GET /api/reportes/{id}/fotos
  └─ DELETE /api/fotos/{id}

PARTE 5: Base de Datos
──────────────────────
☐ Migration SQL
  ├─ ALTER TABLE reportes ADD comentario_resolucion
  ├─ ALTER TABLE reportes ADD comentario_cierre
  ├─ ALTER TABLE reportes ADD fecha_cierre
  ├─ ALTER TABLE reportes ADD operador_cierre_id
  ├─ ALTER TABLE reportes ADD comentario_rechazo
  ├─ ALTER TABLE reportes ADD fecha_rechazo
  ├─ ALTER TABLE reportes ADD operador_rechazo_id
  ├─ ALTER TABLE asignaciones ADD comentario_resolucion
  ├─ CREATE TABLE fotos (...)
  └─ CREATE INDEX idx_fotos_asignacion

PARTE 6: Pruebas
────────────────
☐ Compilar: mvn clean compile
☐ Test unitarios para servicios
☐ Test de integración para endpoints
☐ Test de transacciones y rollback
☐ Validar migraciones SQL

PARTE 7: Documentación
──────────────────────
☐ Actualizar COMO_FUNCIONA.md
☐ Crear api-tests-cierre-con-evidencias.rest
└─ Ejemplos de todos los endpoints
```

---

## 💡 Notas Importantes

```
1. FOTOS: Guardar en servidor/cloud
   ├─ Opción A: AWS S3, GCS, Azure Blob
   ├─ Opción B: Carpeta local /uploads/fotos/
   └─ Recomendación: Cloud (escala mejor)

2. COMENTARIO_RESOLUCION es del TÉCNICO
   ├─ Se asigna cuando técnico marca RESUELTA
   ├─ Visible para Operador en auditoría
   └─ No se puede editar después

3. RECHAZO DE AUDITORÍA tiene 2 niveles:
   ├─ RECHAZADO_AUDITO: Técnico puede reintentar
   └─ RECHAZADO: Final, no hay reintentos

4. NOTIFICACIONES:
   ├─ Técnico: Cuando auditoría es rechazada
   ├─ Operador: Cuando técnico reenvía después de rechazo
   └─ Ciudadano: Al cerrar o rechazar definitivamente

5. AUDITORIA COMPLETA:
   ├─ Todos los campos con timestamp
   ├─ Quién hizo qué y cuándo
   └─ Comentarios de cada etapa
```

