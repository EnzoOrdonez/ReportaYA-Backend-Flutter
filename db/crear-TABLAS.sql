-- ============================================
-- DDL para crear las tablas del proyecto ReportaYA
-- Sistema de Gestión de Reportes Urbanos con Evidencias Fotográficas
-- ============================================
-- Ejecutar en la base de datos desde pgAdmin (Query Tool) como usuario con permisos.
--
-- ÚLTIMA ACTUALIZACIÓN: 21 de noviembre de 2025
-- VERSIÓN: 3.0 - Sistema de Evidencias Fotográficas + Historial + Notificaciones
--
-- NUEVAS CARACTERÍSTICAS:
-- - Tabla 'fotos' para evidencias fotográficas (OBLIGATORIAS)
-- - Tabla 'ubicaciones' para geolocalización de reportes
-- - Tabla 'historial_estados' para auditoría de cambios de estado
-- - Tabla 'token_notificaciones' para FCM tokens de Firebase
-- - Tabla 'mensaje_notificaciones' para plantillas de mensajes
-- - Tabla 'rechazo_mensajes' para motivos de rechazo
-- - Campos de auditoría en 'reportes' (cierre, rechazo, contador)
-- - Campos de evidencias en 'reportes' (comentario_resolucion)
-- - Estado RECHAZADO_AUDITO para reintentos
-- - Índices optimizados para consultas frecuentes
-- ============================================

-- 1) Personas
CREATE TABLE IF NOT EXISTS personas (
  id BIGSERIAL PRIMARY KEY,
  nombres VARCHAR(255) NOT NULL,
  apellidos VARCHAR(255) NOT NULL,
  dni VARCHAR(50) NOT NULL UNIQUE,
  telefono VARCHAR(50) NOT NULL,
  correo VARCHAR(255) NOT NULL UNIQUE
);

-- 2) Cuentas (tabla base para herencia JOINED)
CREATE TABLE IF NOT EXISTS cuentas (
  id BIGSERIAL PRIMARY KEY,
  usuario VARCHAR(255) NOT NULL UNIQUE,
  contrasena_hash VARCHAR(255) NOT NULL,
  persona_id BIGINT NOT NULL UNIQUE,
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE,
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_cuentas_persona FOREIGN KEY (persona_id) REFERENCES personas (id) ON DELETE RESTRICT
);

-- 3) Tablas de subclases (JOINED): operadores_municipales, tecnicos, ciudadanos
CREATE TABLE IF NOT EXISTS operadores_municipales (
  id BIGINT PRIMARY KEY,
  CONSTRAINT fk_op_mun_cuenta FOREIGN KEY (id) REFERENCES cuentas (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tecnicos (
  id BIGINT PRIMARY KEY,
  CONSTRAINT fk_tecnicos_cuenta FOREIGN KEY (id) REFERENCES cuentas (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ciudadanos (
  id BIGINT PRIMARY KEY,
  CONSTRAINT fk_ciudadanos_cuenta FOREIGN KEY (id) REFERENCES cuentas (id) ON DELETE CASCADE
);

-- 4) Ubicaciones
CREATE TABLE IF NOT EXISTS ubicaciones (
  id BIGSERIAL PRIMARY KEY,
  direccion VARCHAR(500) NOT NULL,
  latitud DOUBLE PRECISION,
  longitud DOUBLE PRECISION,
  distrito VARCHAR(255),
  provincia VARCHAR(255),
  departamento VARCHAR(255)
);

-- 5) Reportes (con campos de evidencias y auditoría)
CREATE TABLE IF NOT EXISTS reportes (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(255) NOT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  cuenta_id BIGINT NOT NULL,
  ubicacion_id BIGINT NOT NULL,
  prioridad VARCHAR(50),
  estado VARCHAR(50) NOT NULL,
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE,
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE,
  -- Campos de evidencias (ESCENARIO 1 - Técnico)
  comentario_resolucion VARCHAR(1000),
  -- Campos de auditoría (ESCENARIO 2 y 3 - Operador)
  comentario_cierre VARCHAR(1000),
  fecha_cierre TIMESTAMP WITHOUT TIME ZONE,
  operador_cierre_id BIGINT,
  comentario_rechazo VARCHAR(1000),
  fecha_rechazo TIMESTAMP WITHOUT TIME ZONE,
  operador_rechazo_id BIGINT,
  contador_rechazos INT DEFAULT 0,
  CONSTRAINT fk_reportes_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas (id) ON DELETE CASCADE,
  CONSTRAINT fk_reportes_ubicacion FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones (id) ON DELETE CASCADE,
  CONSTRAINT fk_reportes_operador_cierre FOREIGN KEY (operador_cierre_id) REFERENCES operadores_municipales (id) ON DELETE SET NULL,
  CONSTRAINT fk_reportes_operador_rechazo FOREIGN KEY (operador_rechazo_id) REFERENCES operadores_municipales (id) ON DELETE SET NULL
);

-- 6) Fotos (evidencias fotográficas - OBLIGATORIAS)
CREATE TABLE IF NOT EXISTS fotos (
  id BIGSERIAL PRIMARY KEY,
  reporte_id BIGINT NOT NULL,
  url VARCHAR(500) NOT NULL,
  tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INICIAL', 'PROCESO', 'FINAL')),
  descripcion VARCHAR(255),
  fecha_carga TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_fotos_reporte FOREIGN KEY (reporte_id) REFERENCES reportes (id) ON DELETE CASCADE
);

-- 7) Asignaciones
CREATE TABLE IF NOT EXISTS asignaciones (
  id BIGSERIAL PRIMARY KEY,
  reporte_id BIGINT NOT NULL,
  operador_id BIGINT NOT NULL,
  tecnico_id BIGINT,
  fecha_asignacion TIMESTAMP WITHOUT TIME ZONE,
  fecha_cierre TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT fk_asignaciones_reporte FOREIGN KEY (reporte_id) REFERENCES reportes (id) ON DELETE CASCADE,
  CONSTRAINT fk_asignaciones_operador FOREIGN KEY (operador_id) REFERENCES operadores_municipales (id) ON DELETE RESTRICT,
  CONSTRAINT fk_asignaciones_tecnico FOREIGN KEY (tecnico_id) REFERENCES tecnicos (id) ON DELETE SET NULL
);

-- 8) Historial de Estados (auditoría de cambios)
CREATE TABLE IF NOT EXISTS historial_estados (
  id BIGSERIAL PRIMARY KEY,
  reporte_id BIGINT NOT NULL,
  estado_anterior VARCHAR(50),
  estado_nuevo VARCHAR(50) NOT NULL,
  fecha_cambio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_historial_reporte FOREIGN KEY (reporte_id) REFERENCES reportes (id) ON DELETE CASCADE
);

-- 9) Token de Notificaciones (FCM tokens para Firebase)
CREATE TABLE IF NOT EXISTS token_notificaciones (
  id BIGSERIAL PRIMARY KEY,
  cuenta_id BIGINT NOT NULL UNIQUE,
  token VARCHAR(512) NOT NULL,
  CONSTRAINT fk_token_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas (id) ON DELETE CASCADE
);

-- 10) Mensajes de Notificación (plantillas por estado)
CREATE TABLE IF NOT EXISTS mensaje_notificaciones (
  id BIGSERIAL PRIMARY KEY,
  estado VARCHAR(50) NOT NULL UNIQUE,
  mensaje VARCHAR(500) NOT NULL
);

-- 11) Mensajes de Rechazo (motivos de rechazo de reportes)
CREATE TABLE IF NOT EXISTS rechazo_mensajes (
  id BIGSERIAL PRIMARY KEY,
  reporte_id BIGINT NOT NULL,
  motivo VARCHAR(1000) NOT NULL,
  fecha_rechazo TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_rechazo_reporte FOREIGN KEY (reporte_id) REFERENCES reportes (id) ON DELETE CASCADE
);

-- 12) Índices para optimización de consultas
-- Índice parcial: impedir más de una asignación abierta por reporte
CREATE UNIQUE INDEX IF NOT EXISTS ux_asignaciones_reporte_abierta ON asignaciones (reporte_id) WHERE fecha_cierre IS NULL;

-- Índices para reportes
CREATE INDEX IF NOT EXISTS idx_reportes_cuenta_id ON reportes (cuenta_id);
CREATE INDEX IF NOT EXISTS idx_reportes_ubicacion_id ON reportes (ubicacion_id);
CREATE INDEX IF NOT EXISTS idx_reportes_estado ON reportes (estado);
CREATE INDEX IF NOT EXISTS idx_reportes_fecha_creacion ON reportes (fecha_creacion DESC);

-- Índices para asignaciones
CREATE INDEX IF NOT EXISTS idx_asignaciones_reporte_id ON asignaciones (reporte_id);
CREATE INDEX IF NOT EXISTS idx_asignaciones_tecnico_id ON asignaciones (tecnico_id);
CREATE INDEX IF NOT EXISTS idx_asignaciones_operador_id ON asignaciones (operador_id);

-- Índices para fotos (evidencias)
CREATE INDEX IF NOT EXISTS idx_fotos_reporte_id ON fotos (reporte_id);
CREATE INDEX IF NOT EXISTS idx_fotos_tipo ON fotos (tipo);

-- Índices para historial de estados (auditoría)
CREATE INDEX IF NOT EXISTS idx_historial_reporte_id ON historial_estados (reporte_id);
CREATE INDEX IF NOT EXISTS idx_historial_fecha_cambio ON historial_estados (fecha_cambio DESC);

-- Índices para notificaciones
CREATE INDEX IF NOT EXISTS idx_token_cuenta_id ON token_notificaciones (cuenta_id);

-- Índices para rechazo
CREATE INDEX IF NOT EXISTS idx_rechazo_reporte_id ON rechazo_mensajes (reporte_id);


-- ============================================
-- SCRIPTS DE LIMPIEZA (usar con precaución)
-- ============================================
-- Descomenta estas líneas para eliminar todas las tablas y empezar desde cero
/*
DROP TABLE IF EXISTS rechazo_mensajes CASCADE;
DROP TABLE IF EXISTS mensaje_notificaciones CASCADE;
DROP TABLE IF EXISTS token_notificaciones CASCADE;
DROP TABLE IF EXISTS historial_estados CASCADE;
DROP TABLE IF EXISTS fotos CASCADE;
DROP TABLE IF EXISTS asignaciones CASCADE;
DROP TABLE IF EXISTS reportes CASCADE;
DROP TABLE IF EXISTS ubicaciones CASCADE;
DROP TABLE IF EXISTS operadores_municipales CASCADE;
DROP TABLE IF EXISTS tecnicos CASCADE;
DROP TABLE IF EXISTS ciudadanos CASCADE;
DROP TABLE IF EXISTS cuentas CASCADE;
DROP TABLE IF EXISTS personas CASCADE;
*/

-- ============================================
-- COMENTARIOS SOBRE LA ESTRUCTURA
-- ============================================
/*
FLUJO DE ESTADOS DEL REPORTE:
1. PENDIENTE     → Reporte creado por ciudadano
2. REVISION      → Operador revisa y prioriza
3. PROCESO       → Técnico asignado y trabajando
4. RESUELTA      → Técnico marcó como resuelta (CON fotos + comentario OBLIGATORIOS)
5. CERRADA       → Operador aprobó (auditoría exitosa)
6. RECHAZADO_AUDITO → Operador rechazó (técnico puede reintentar, max 3 veces)
7. RECHAZADO     → Rechazo definitivo (contador_rechazos >= 3)

CAMPOS DE EVIDENCIAS (Técnico):
- comentario_resolucion: Comentario OBLIGATORIO del técnico (10-1000 caracteres)
- fotos: Tabla relacionada, mínimo 1 foto OBLIGATORIA antes de marcar RESUELTA

CAMPOS DE AUDITORÍA (Operador):
- comentario_cierre: Comentario al aprobar (CERRADA)
- fecha_cierre: Timestamp del cierre
- operador_cierre_id: Operador que aprobó
- comentario_rechazo: Comentario al rechazar (RECHAZADO_AUDITO)
- fecha_rechazo: Timestamp del rechazo
- operador_rechazo_id: Operador que rechazó
- contador_rechazos: Contador de rechazos (max 3)

TIPOS DE FOTO:
- INICIAL: Foto antes de iniciar el trabajo
- PROCESO: Foto durante el trabajo
- FINAL: Foto al completar el trabajo

NUEVAS TABLAS DEL MERGE (Main Branch):

historial_estados:
- Registra CADA cambio de estado del reporte (auditoría completa)
- Campos: reporte_id, estado_anterior, estado_nuevo, fecha_cambio
- Se crea automáticamente cuando se llama a reporteService.cambiarEstadoReporte()

token_notificaciones:
- Almacena tokens FCM de Firebase para push notifications
- Relación 1:1 con cuentas (un token por usuario)
- Se actualiza cuando el usuario registra su dispositivo

mensaje_notificaciones:
- Plantillas de mensajes personalizados por estado
- Relación 1:1 con EstadoReporte (un mensaje por estado)
- Ejemplo: RESUELTA → "Tu reporte fue resuelto"

rechazo_mensajes:
- Almacena motivos de rechazo de reportes
- Complementa el campo comentario_rechazo en reportes
- Permite historial de rechazos detallado
*/