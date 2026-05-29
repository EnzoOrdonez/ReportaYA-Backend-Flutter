# ReportaYA — Backend (incidencia-urbana)

Backend Spring Boot del sistema de reportes de incidencias urbanas. Proyecto académico — curso Programación Móvil, Universidad de Lima.

## Stack

- **Spring Boot** 3.5.6 — Java 17 (compila con `--release 17`; corre con JDK 17 o superior)
- **Hibernate ORM** 6.x — JPA + auto-detección de dialecto
- **PostgreSQL** (Neon Azure) en dev; H2 disponible para pruebas
- **JWT** (jjwt 0.12.3) HS256 + **BCrypt** (spring-security-crypto) para passwords
- **Firebase Admin SDK** 9.2.0 — notificaciones push (opcional, ver `FIREBASE-SETUP.md`)
- **Maven Wrapper** incluido (`mvnw` / `mvnw.cmd`) — no requiere Maven instalado

## Requisitos previos

- **JDK 17 o superior** instalado y `JAVA_HOME` apuntando a él
  - Verifica: `java -version` y `echo $env:JAVA_HOME` (PowerShell) o `echo $JAVA_HOME` (bash)
- Acceso a una instancia **PostgreSQL** — la Neon compartida del equipo, o local 17.x
- Cliente HTTP para probar endpoints: **REST Client** (VS Code) o **HTTP Client** (IntelliJ)

## Setup

```powershell
# 1. Clonar
git clone <URL-del-repo>
cd ReportaYA-Backend-Flutter

# 2. Copiar el template de configuración local
copy application-local.properties.example application-local.properties
# (en bash: cp application-local.properties.example application-local.properties)

# 3. Editar application-local.properties con las credenciales reales.
#    Pídelas al equipo por canal seguro (NO chat público, NO commits).

# 4. Arrancar
.\mvnw.cmd spring-boot:run
# (en bash: ./mvnw spring-boot:run)
```

App escucha en **`http://localhost:8081`** en modo dev. (El `Dockerfile` expone `8080` — ver `README_DOCKER.md`.)

**Si `mvnw` no encuentra `java`:** confirma que `JAVA_HOME` está seteado a un JDK 17+. En Windows persistente:
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot", "User")
```

## Estructura del proyecto

```
ReportaYA-Backend-Flutter/
├── src/main/java/com/ulima/incidenciaurbana/
│   ├── IncidenciaUrbanaApplication.java   # main class
│   ├── config/         # configuración Spring (Firebase, beans de seguridad)
│   ├── controller/     # endpoints REST
│   ├── dto/            # objetos de transferencia
│   ├── exception/      # excepciones de dominio
│   ├── factory/        # Abstract Factory para cuentas de usuario
│   ├── model/          # entidades JPA
│   ├── repository/     # repositorios Spring Data JPA
│   ├── service/        # interfaces de servicio
│   │   └── impl/       # implementaciones
│   └── util/           # utilidades (JwtUtil, etc.)
├── src/main/resources/
│   └── application.properties             # config base con placeholders ${...}
├── db/                                    # scripts SQL (esquema + datos)
├── diagrams/                              # PlantUML (modelo BD, flujo de estados)
├── doc/                                   # documentación técnica + capturas
├── application-local.properties.example   # template para creds locales
├── Dockerfile
└── pom.xml
```

## Roles y flujo de estados

Tres actores: **Ciudadano** (reporta), **Técnico** (atiende), **Operador Municipal** (supervisa y cierra).

```
PENDIENTE → REVISION → PROCESO → RESUELTA → CERRADA
                                          ↘ RECHAZADO
```

Detalle del flujo en `diagrams/flujo_cambio_estado.puml` y `RESUMEN_SPRINT_2.md`.

## Pruebas de endpoints

Archivos `.rest` ejecutables con REST Client / HTTP Client:

- `api-tests-auth.rest` — login, registro, JWT
- `api-tests-flujo-completo.rest` — flujo end-to-end de un reporte
- `ESCENARIO-3-RECHAZOS-Y-DEFINITIVO-SIMPLIFICADO.rest` — caso de rechazos
- `TEST-ENDPOINT-CERRAR-REPORTE.rest` — cierre de reporte con evidencias

## Base de datos inicial

Scripts en `db/`:
- `crear-DB.sql` — crea la base
- `crear-TABLAS.sql` — esquema
- `llenar-TABLAS.sql` — datos de prueba
- `mensajes_notificacion.sql` — catálogo de mensajes

Si usas Neon (compartida), Hibernate (`ddl-auto=update`) ya mantiene el esquema; los scripts sirven como referencia o para una BD local desde cero.

## Docker

Build + run resumido en `README_DOCKER.md`. Resumen:
```bash
docker build -t reportaya-backend .
docker run -p 8080:8080 \
  -e JWT_SECRET=... \
  -e SPRING_DATASOURCE_URL=... \
  -e SPRING_DATASOURCE_USERNAME=... \
  -e SPRING_DATASOURCE_PASSWORD=... \
  reportaya-backend
```
Los secretos **no** van en la imagen; se pasan en runtime.

## Firebase (opcional)

App funciona sin Firebase. Para activar notificaciones push y almacenamiento de fotos en la nube:
- `FIREBASE-SETUP.md` — setup del service account
- `FIREBASE_STORAGE_GUIDE.md` — subida de imágenes

El service account va en `src/main/resources/firebase-service-account.json` (gitignored — cada dev usa su propia copia, no se sube al repo).

## Documentación adicional

- `ARQUITECTURA_SOLID.md` — diseño SOLID, endpoints
- `RESUMEN_SPRINT_2.md` — overview por paquetes
- `doc/proyecto.md` — alcance funcional completo
- `doc/ANALISIS_CIERRE_CON_EVIDENCIAS.md` — caso de uso cierre con foto
- `diagrams/` — modelo de datos y flujos en PlantUML

## Notas para el equipo

- **Nunca** commitees `application-local.properties` ni secretos en código. Si te equivocas, avisa de inmediato para rotar.
- `.gitattributes` fuerza **LF** como line ending. IntelliJ/VS Code lo respetan por defecto; si tu editor inserta CRLF, ajústalo.
- Pull requests para `main`; no merges directos.
