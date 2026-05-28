-- ==========================================
-- Script de Datos de Prueba - ReportaYA
-- Versión: 4.0
-- Fecha: 21 de noviembre de 2025
-- Descripción: Reportes en estado REVISION + datos de Historial y Notificaciones
-- ==========================================

-- Limpia todas las tablas en orden correcto
TRUNCATE TABLE
  rechazo_mensajes,
  mensaje_notificaciones,
  token_notificaciones,
  historial_estados,
  fotos,
  asignaciones,
  reportes,
  ubicaciones,
  operadores_municipales,
  tecnicos,
  ciudadanos,
  cuentas,
  personas
RESTART IDENTITY CASCADE;

-- 1) Operador Municipal (Ana Garcia)
WITH p AS (
  INSERT INTO personas (nombres, apellidos, dni, telefono, correo)
  VALUES ('Ana', 'Garcia', '73456789', '999000111', 'ana.garcia@example.com')
  ON CONFLICT (dni) DO UPDATE SET nombres = EXCLUDED.nombres, apellidos = EXCLUDED.apellidos, telefono = EXCLUDED.telefono, correo = EXCLUDED.correo
  RETURNING id
)
, c AS (
  INSERT INTO cuentas (usuario, contrasena_hash, persona_id, fecha_creacion, fecha_actualizacion, activo)
  SELECT 'op_ana', 'hash_demo', id, now(), now(), true FROM p
  ON CONFLICT (persona_id) DO NOTHING
  RETURNING id
)
INSERT INTO operadores_municipales (id)
SELECT id FROM c;

-- Fallback: si la cuenta ya existía, asegurar la fila en operadores_municipales
INSERT INTO operadores_municipales (id)
SELECT c2.id FROM cuentas c2
JOIN personas p2 ON p2.id = c2.persona_id
WHERE p2.dni = '73456789'
ON CONFLICT DO NOTHING;

-- 2) Tecnico (Luis Torres)
WITH p AS (
  INSERT INTO personas (nombres, apellidos, dni, telefono, correo)
  VALUES ('Luis', 'Torres', '71234567', '988777666', 'luis.torres@example.com')
  ON CONFLICT (dni) DO UPDATE SET nombres = EXCLUDED.nombres, apellidos = EXCLUDED.apellidos, telefono = EXCLUDED.telefono, correo = EXCLUDED.correo
  RETURNING id
)
, c AS (
  INSERT INTO cuentas (usuario, contrasena_hash, persona_id, fecha_creacion, fecha_actualizacion, activo)
  SELECT 'tec_luis', 'hash_demo', id, now(), now(), true FROM p
  ON CONFLICT (persona_id) DO NOTHING
  RETURNING id
)
INSERT INTO tecnicos (id)
SELECT id FROM c;

INSERT INTO tecnicos (id)
SELECT c2.id FROM cuentas c2
JOIN personas p2 ON p2.id = c2.persona_id
WHERE p2.dni = '71234567'
ON CONFLICT DO NOTHING;

-- 3) Ciudadano (Carlos Perez)
WITH p AS (
  INSERT INTO personas (nombres, apellidos, dni, telefono, correo)
  VALUES ('Carlos', 'Perez', '70011223', '977666555', 'carlos.perez@example.com')
  ON CONFLICT (dni) DO UPDATE SET nombres = EXCLUDED.nombres, apellidos = EXCLUDED.apellidos, telefono = EXCLUDED.telefono, correo = EXCLUDED.correo
  RETURNING id
)
, c AS (
  INSERT INTO cuentas (usuario, contrasena_hash, persona_id, fecha_creacion, fecha_actualizacion, activo)
  SELECT 'ci_carlos', 'hash_demo', id, now(), now(), true FROM p
  ON CONFLICT (persona_id) DO NOTHING
  RETURNING id
)
INSERT INTO ciudadanos (id)
SELECT id FROM c;

INSERT INTO ciudadanos (id)
SELECT c2.id FROM cuentas c2
JOIN personas p2 ON p2.id = c2.persona_id
WHERE p2.dni = '70011223'
ON CONFLICT DO NOTHING;

-- ==========================================
-- UBICACIONES
-- ==========================================

INSERT INTO ubicaciones (direccion, latitud, longitud, distrito, provincia, departamento) VALUES
('Av. Arequipa 1234, Miraflores', -12.120000, -77.030000, 'Miraflores', 'Lima', 'Lima'),
('Jr. de la Unión 456, Cercado de Lima', -12.046374, -77.042793, 'Cercado de Lima', 'Lima', 'Lima'),
('Av. Javier Prado 2850, San Isidro', -12.095000, -77.035000, 'San Isidro', 'Lima', 'Lima'),
('Calle Las Flores 789, Surco', -12.140000, -77.015000, 'Santiago de Surco', 'Lima', 'Lima'),
('Av. Venezuela 567, Breña', -12.060000, -77.050000, 'Breña', 'Lima', 'Lima');

-- ==========================================
-- REPORTES EN ESTADO REVISION (listos para asignar a técnicos)
-- ==========================================

INSERT INTO reportes (
  titulo, descripcion, prioridad, estado, 
  fecha_creacion, cuenta_id, ubicacion_id, contador_rechazos
) VALUES 
('Semáforo malogrado en Av. Arequipa', 'El semáforo no funciona desde hace 2 días', 'ALTA', 'REVISION', NOW(), (SELECT id FROM ciudadanos LIMIT 1), 1, 0),
('Bache peligroso en Jr. de la Unión', 'Bache profundo que causa accidentes', 'ALTA', 'REVISION', NOW(), (SELECT id FROM ciudadanos LIMIT 1), 2, 0),
('Fuga de agua en Av. Javier Prado', 'Fuga importante en tubería principal', 'ALTA', 'REVISION', NOW(), (SELECT id FROM ciudadanos LIMIT 1), 3, 0),
('Luminaria quemada en Calle Las Flores', 'Poste de luz sin funcionamiento', 'MEDIA', 'REVISION', NOW(), (SELECT id FROM ciudadanos LIMIT 1), 4, 0),
('Pavimento irregular en Av. Venezuela', 'Superficie muy irregular', 'MEDIA', 'REVISION', NOW(), (SELECT id FROM ciudadanos LIMIT 1), 5, 0),
('Señalización borrada en crucero', 'Líneas de paso peatonal borradas', 'BAJA', 'REVISION', NOW(), (SELECT id FROM ciudadanos LIMIT 1), 1, 0);

-- ==========================================
-- FOTOS INICIALES (DESPUÉS de los reportes)
-- ==========================================

-- Fotos Reporte 1 (Semáforo)
INSERT INTO fotos (reporte_id, url, tipo, descripcion, fecha_carga) VALUES
(1, 'uploads/fotos/reporte-1-inicial-001.jpg', 'INICIAL', 'Semáforo malogrado - vista frontal', NOW()),
(1, 'uploads/fotos/reporte-1-inicial-002.jpg', 'INICIAL', 'Semáforo sin funcionar - se ve oscuro', NOW());

-- Fotos Reporte 2 (Bache)
INSERT INTO fotos (reporte_id, url, tipo, descripcion, fecha_carga) VALUES
(2, 'uploads/fotos/reporte-2-inicial-001.jpg', 'INICIAL', 'Bache profundo en la vía', NOW()),
(2, 'uploads/fotos/reporte-2-inicial-002.jpg', 'INICIAL', 'Profundidad del bache - peligroso', NOW()),
(2, 'uploads/fotos/reporte-2-inicial-003.jpg', 'INICIAL', 'Vista lateral del daño', NOW());

-- Fotos Reporte 3 (Fuga de agua)
INSERT INTO fotos (reporte_id, url, tipo, descripcion, fecha_carga) VALUES
(3, 'uploads/fotos/reporte-3-inicial-001.jpg', 'INICIAL', 'Fuga de agua en tubería', NOW()),
(3, 'uploads/fotos/reporte-3-inicial-002.jpg', 'INICIAL', 'Agua brotando del punto de fuga', NOW());

-- Fotos Reporte 4 (Luminaria)
INSERT INTO fotos (reporte_id, url, tipo, descripcion, fecha_carga) VALUES
(4, 'uploads/fotos/reporte-4-inicial-001.jpg', 'INICIAL', 'Luminaria quemada sin funcionar', NOW()),
(4, 'uploads/fotos/reporte-4-inicial-002.jpg', 'INICIAL', 'Zona oscura sin iluminación', NOW());

-- Fotos Reporte 5 (Pavimento)
INSERT INTO fotos (reporte_id, url, tipo, descripcion, fecha_carga) VALUES
(5, 'uploads/fotos/reporte-5-inicial-001.jpg', 'INICIAL', 'Pavimento irregular y dañado', NOW()),
(5, 'uploads/fotos/reporte-5-inicial-002.jpg', 'INICIAL', 'Detalle de las grietas', NOW()),
(5, 'uploads/fotos/reporte-5-inicial-003.jpg', 'INICIAL', 'Riesgo de tropezar en el área', NOW());

-- Fotos Reporte 6 (Señalización)
INSERT INTO fotos (reporte_id, url, tipo, descripcion, fecha_carga) VALUES
(6, 'uploads/fotos/reporte-6-inicial-001.jpg', 'INICIAL', 'Líneas de paso peatonal borradas', NOW()),
(6, 'uploads/fotos/reporte-6-inicial-002.jpg', 'INICIAL', 'Vista general del crucero sin señalización', NOW());

-- ==========================================
-- HISTORIAL DE ESTADOS (registros iniciales)
-- ==========================================

-- Cada reporte tiene un registro inicial: null → REVISION
INSERT INTO historial_estados (reporte_id, estado_anterior, estado_nuevo, fecha_cambio) VALUES
(1, NULL, 'PENDIENTE', NOW() - INTERVAL '2 days'),
(1, 'PENDIENTE', 'REVISION', NOW() - INTERVAL '1 day'),
(2, NULL, 'PENDIENTE', NOW() - INTERVAL '2 days'),
(2, 'PENDIENTE', 'REVISION', NOW() - INTERVAL '1 day'),
(3, NULL, 'PENDIENTE', NOW() - INTERVAL '2 days'),
(3, 'PENDIENTE', 'REVISION', NOW() - INTERVAL '1 day'),
(4, NULL, 'PENDIENTE', NOW() - INTERVAL '2 days'),
(4, 'PENDIENTE', 'REVISION', NOW() - INTERVAL '1 day'),
(5, NULL, 'PENDIENTE', NOW() - INTERVAL '2 days'),
(5, 'PENDIENTE', 'REVISION', NOW() - INTERVAL '1 day'),
(6, NULL, 'PENDIENTE', NOW() - INTERVAL '2 days'),
(6, 'PENDIENTE', 'REVISION', NOW() - INTERVAL '1 day');

-- ==========================================
-- TOKENS DE NOTIFICACIONES (FCM)
-- ==========================================

-- Registrar tokens FCM para ciudadanos (simulados)
INSERT INTO token_notificaciones (cuenta_id, token) VALUES
((SELECT id FROM ciudadanos LIMIT 1), 'fcm_token_ciudadano_carlos_12345');

-- ==========================================
-- MENSAJES DE NOTIFICACIÓN (Plantillas)
-- ==========================================

INSERT INTO mensaje_notificaciones (estado, mensaje) VALUES
('PENDIENTE', 'Tu reporte ha sido creado y está pendiente de revisión'),
('REVISION', 'Tu reporte está siendo revisado por un operador municipal'),
('PROCESO', 'Un técnico ha sido asignado y está trabajando en tu reporte'),
('RESUELTA', 'Tu reporte ha sido resuelto. Está en proceso de auditoría'),
('CERRADA', 'Tu reporte ha sido cerrado exitosamente. ¡Gracias por tu colaboración!'),
('RECHAZADO_AUDITO', 'Tu reporte requiere correcciones. El técnico puede reintentar'),
('RECHAZADO', 'Tu reporte ha sido rechazado definitivamente');

-- ==========================================
-- CONSULTAS DE VERIFICACIÓN
-- ==========================================

SELECT '=== PERSONAS ===' AS seccion;
SELECT id, nombres, apellidos, dni FROM personas;

SELECT '=== CUENTAS ===' AS seccion;
SELECT id, usuario, persona_id FROM cuentas;

SELECT '=== TECNICOS ===' AS seccion;
SELECT id FROM tecnicos;

SELECT '=== OPERADORES ===' AS seccion;
SELECT id FROM operadores_municipales;

SELECT '=== UBICACIONES ===' AS seccion;
SELECT id, direccion, distrito FROM ubicaciones;

SELECT '=== REPORTES EN REVISION ===' AS seccion;
SELECT id, titulo, prioridad, estado, cuenta_id, ubicacion_id
FROM reportes
ORDER BY id;

SELECT '=== FOTOS POR REPORTE ===' AS seccion;
SELECT r.id AS reporte_id, r.titulo, COUNT(f.id) AS total_fotos,
       STRING_AGG(f.tipo, ', ') AS tipos
FROM reportes r
LEFT JOIN fotos f ON f.reporte_id = r.id
GROUP BY r.id, r.titulo
ORDER BY r.id;

SELECT '=== HISTORIAL DE ESTADOS ===' AS seccion;
SELECT h.id, h.reporte_id, h.estado_anterior, h.estado_nuevo, h.fecha_cambio
FROM historial_estados h
ORDER BY h.reporte_id, h.fecha_cambio;

SELECT '=== TOKENS DE NOTIFICACIÓN ===' AS seccion;
SELECT t.id, t.cuenta_id, LEFT(t.token, 30) || '...' AS token_preview
FROM token_notificaciones t;

SELECT '=== MENSAJES DE NOTIFICACIÓN ===' AS seccion;
SELECT m.estado, m.mensaje
FROM mensaje_notificaciones m
ORDER BY 
  CASE m.estado
    WHEN 'PENDIENTE' THEN 1
    WHEN 'REVISION' THEN 2
    WHEN 'PROCESO' THEN 3
    WHEN 'RESUELTA' THEN 4
    WHEN 'CERRADA' THEN 5
    WHEN 'RECHAZADO_AUDITO' THEN 6
    WHEN 'RECHAZADO' THEN 7
  END;