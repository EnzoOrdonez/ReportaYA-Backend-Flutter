# 📋 Sistema ReportaYA - Backend API REST

## 🎯 Descripción del Proyecto

**ReportaYA** es un sistema de gestión de reportes urbanos diseñado para facilitar la comunicación entre ciudadanos y las autoridades municipales. La aplicación permite a los ciudadanos reportar incidencias urbanas (como baches, basura acumulada, semáforos dañados, etc.) y a las autoridades municipales gestionar estos reportes de manera eficiente a través de un flujo de trabajo estructurado.

### 🏢 Arquitectura del Sistema

El backend está desarrollado como una **API REST** utilizando **Spring Boot 3.5.6** con **Java 17**, siguiendo los principios **SOLID** y patrones de diseño como **Abstract Factory** para la gestión de diferentes tipos de cuentas de usuario.

## 🎭 Actores del Sistema

### 👥 Tipos de Usuarios

1. **👤 Ciudadano**
   - Puede crear reportes de incidencias urbanas
   - Consulta el estado de sus reportes
   - Proporciona ubicación geográfica y descripción detallada

2. **⚙️ Técnico**
   - Recibe asignaciones de reportes para resolver
   - Actualiza el estado de los trabajos asignados
   - Ejecuta las tareas de campo

3. **👨‍💼 Operador Municipal**
   - Revisa los reportes enviados por ciudadanos
   - Asigna técnicos a los reportes según disponibilidad y especialidad
   - Gestiona la prioridad de los reportes
   - Supervisa el flujo de trabajo

## 🔄 Flujo de Trabajo del Sistema

```
Ciudadano crea reporte → PENDIENTE → Operador revisa → REVISION → 
Operador asigna técnico → PROCESO → Técnico resuelve → RESUELTA   → 
Operador audita la resolucion del reporte → CERRADA 
```

### 📋 Estados de un Reporte

- **PENDIENTE**: Reporte recién creado, esperando revisión
- **REVISION**: Operador municipal está evaluando el reporte
- **PROCESO**: Técnico asignado está trabajando en la resolución
- **RESUELTA**: Técnico ha completado el trabajo
- **CERRADA**: Operador audita y cierra definitivamente el caso
- **RECHAZADO**: Reporte no válido o duplicado

### 🎯 Sistema de Triaje

El **Triaje** es el proceso central donde el Operador Municipal clasifica y asigna técnicos a los reportes. Para más información detallada sobre cómo funciona el sistema de asignación, consulta:

- **[COMO_FUNCIONA.md](./COMO_FUNCIONA.md)** ⭐ **COMIENZA AQUÍ** - Guía completa del sistema
- **[TRIAJE_SISTEMA_ASIGNACION.md](./TRIAJE_SISTEMA_ASIGNACION.md)** - Explicación técnica detallada
- **[TRIAJE_VISUAL_RESUMEN.md](./TRIAJE_VISUAL_RESUMEN.md)** - Resumen visual y ejemplos
- **[TRIAJE_GUIA_RAPIDA.md](./TRIAJE_GUIA_RAPIDA.md)** - Guía rápida en 5 minutos
- **[CAMBIOS_TRIAJE_PRIORIDAD.md](./CAMBIOS_TRIAJE_PRIORIDAD.md)** - Cambios recientes implementados

Archivos de ejemplo:
- **api-tests-triaje-ejemplos.rest** - Ejemplos REST para probar el sistema
- **api-tests-triaje-prioridad.rest** - Ejemplos con prioridad obligatoria

### 🚨 Niveles de Prioridad

- **BAJA**: Incidencias menores que no afectan significativamente la vida urbana
- **MEDIA**: Problemas moderados que requieren atención oportuna
- **ALTA**: Situaciones urgentes que afectan la seguridad o servicios esenciales

## 🗃️ Modelo de Datos

### 📊 Entidades Principales

#### 👤 Persona
- Información personal básica (nombres, apellidos, DNI, teléfono, correo)
- Entidad base para todos los usuarios del sistema

#### 🔐 Cuenta (Herencia JOINED)
- Sistema de autenticación con usuario y contraseña
- Herencia implementada con estrategia JOINED en JPA
- Subclases: Ciudadano, Técnico, OperadorMunicipal

#### 📍 Ubicación
- Coordenadas geográficas (latitud, longitud)
- Dirección descriptiva opcional
- Relación uno a uno con Reporte

#### 📋 Reporte
- Información del incidente (título, descripción)
- Estado y prioridad
- Fechas de creación y actualización
- Ubicación obligatoria

#### 📝 Asignación
- Relación entre reporte, operador y técnico
- Control de fechas (asignación, aceptación, cierre)
- Garantiza una sola asignación activa por reporte

## 🏗️ Arquitectura Técnica

### 🎯 Patrones de Diseño Implementados

#### 🏭 Abstract Factory Pattern
```java
// Factory para crear diferentes tipos de cuentas
public interface CuentaFactory {
    Cuenta crearCuenta(String usuario, String contrasenaHash, Persona persona);
    String getTipoCuenta();
}

// Implementaciones específicas
@Component
public class CiudadanoFactory implements CuentaFactory { ... }

@Component  
public class TecnicoFactory implements CuentaFactory { ... }

@Component
public class OperadorMunicipalFactory implements CuentaFactory { ... }
```

#### 🎛️ Service Layer Pattern
- Separación clara entre controladores y lógica de negocio
- Interfaces de servicio para facilitar testing y mantenibilidad
- Implementaciones específicas con transaccionalidad

#### 📦 DTO Pattern
- Transferencia de datos entre capas sin exponer entidades
- Validaciones específicas para requests
- Transformación controlada de datos

### 🛠️ Stack Tecnológico

- **☕ Java 17**: Versión LTS con características modernas
- **🌱 Spring Boot 3.5.6**: Framework principal con auto-configuración
- **🗄️ Spring Data JPA**: Persistencia con Hibernate
- **🐘 PostgreSQL**: Base de datos principal
- **🧪 H2**: Base de datos en memoria para testing
- **🔐 JWT**: Tokens para autenticación (preparado para implementar)
- **🐳 Docker**: Containerización para despliegue

### 🌐 API REST Endpoints

#### 🔐 Autenticación
```http
POST /api/auth/login - Autenticación de usuarios
```

#### 👥 Gestión Unificada de Cuentas
```http
POST /api/cuenta - Crear cuenta (cualquier tipo: CIUDADANO, TECNICO, OPERADOR_MUNICIPAL)
```

#### 👤 Ciudadanos
```http
GET    /api/ciudadanos/{id}    - Obtener ciudadano por ID
PUT    /api/ciudadanos/{id}    - Actualizar datos
DELETE /api/ciudadanos/{id}    - Eliminar cuenta
```

#### 📋 Reportes
```http
POST   /api/reportes                      - Crear reporte
GET    /api/reportes                      - Listar todos (paginado)
GET    /api/reportes/cuenta/{cuentaId}    - Reportes de un ciudadano
PUT    /api/reportes/{id}                 - Actualizar reporte
PATCH  /api/reportes/{id}/estado          - Cambiar estado
PATCH  /api/reportes/{id}/prioridad       - Cambiar prioridad
DELETE /api/reportes/{id}                 - Eliminar reporte
```

#### ⚙️ Técnicos
```http
GET /api/tecnicos?page=0 - Listar técnicos (paginado)
```

#### 📝 Asignaciones
```http
POST /api/asignaciones - Crear asignación (triaje)
```

## 💾 Base de Datos

### 🏗️ Estructura
- **PostgreSQL** como base de datos principal
- **Herencia JOINED** para el sistema de cuentas
- **Índices optimizados** para consultas frecuentes
- **Restricciones de integridad** para mantener consistencia

### 📊 Scripts de Base de Datos
- `crear-DB.sql`: Creación de base de datos y usuario
- `crear-TABLAS.sql`: DDL completo con todas las tablas
- `llenar-TABLAS.sql`: Datos de prueba con usuarios base

## 🔧 Configuración y Despliegue

### 🌍 Variables de Entorno
```properties
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/database
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=contraseña

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

### 🐳 Docker
- **Multi-stage build** para optimizar imagen
- **Usuario no-root** para seguridad
- **Variables de entorno** configurables
- **Imagen base Alpine** para menor tamaño

```dockerfile
# Build con Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Runtime con JRE Alpine
FROM eclipse-temurin:17-jre-alpine
```

## 🧪 Testing y Validación

### 📋 Archivos de Prueba REST
- `api-tests-auth.rest`: Pruebas de autenticación
- `api-tests-flujo-completo.rest`: Flujo completo del sistema

### 🔍 Validaciones Implementadas
- **Validación de datos** con Jakarta Validation
- **Unicidad** de DNI, correo y usuario
- **Integridad referencial** en asignaciones
- **Estados válidos** en transiciones de reportes

## 🎯 Características Destacadas

### ✅ Funcionalidades Principales
- ✅ **Sistema de autenticación** básico implementado
- ✅ **Gestión completa de reportes** con ubicación geográfica
- ✅ **Flujo de asignación** operador → técnico
- ✅ **Paginación** en consultas masivas
- ✅ **Validaciones robustas** de datos
- ✅ **API REST documentada** con archivos .rest

### 🚀 Preparado para Futuras Mejoras
- 🔄 **Autenticación JWT** (dependencias incluidas)
- 🔄 **Upload de imágenes** (modelo preparado)
- 🔄 **Notificaciones en tiempo real**
- 🔄 **Dashboard administrativo**
- 🔄 **API de geolocalización integrada**

## 📁 Estructura del Proyecto

```
src/main/java/com/ulima/incidenciaurbana/
├── 🎛️ controller/          # Controladores REST
├── 📋 dto/                # Objetos de transferencia
├── 🏭 factory/            # Abstract Factory para cuentas
├── 📊 model/              # Entidades JPA
├── 🗃️ repository/         # Repositorios Spring Data
├── 🔧 service/            # Lógica de negocio
└── ⚠️ exception/         # Excepciones personalizadas
```

## 🎭 Conclusión

**ReportaYA** es un sistema robusto y escalable que facilita la gestión de incidencias urbanas mediante una API REST bien estructurada. La implementación sigue buenas prácticas de desarrollo, utiliza patrones de diseño apropiados y está preparado tanto para entornos de desarrollo como de producción a través de Docker.

El sistema proporciona una base sólida para la comunicación efectiva entre ciudadanos y autoridades municipales, mejorando la calidad de vida urbana a través de la tecnología.