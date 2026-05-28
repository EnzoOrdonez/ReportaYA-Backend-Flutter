# Pruebas de Software - ReportaYA Backend

## Estructura de Pruebas (JUnit 5)

Este directorio contiene pruebas de software organizadas por Historia de Usuario (HU).

### HU1: Mapa de Reportes con Filtros
- **Caja Blanca**: Prueba de `validarEstadoReporte()` con complejidad ciclomática > 3
- **Caja Negra**: Prueba de filtrado de reportes (> 4 campos)
- **Unitarias**: 4 pruebas del servicio `filtrarReportes()`

### HU2: Creación de Reportes con Ubicación
- **Caja Blanca**: Prueba de validación de coordenadas con complejidad > 3
- **Caja Negra**: Prueba de `CrearReporteDTO` (> 4 campos)
- **Unitarias**: 4 pruebas del servicio de reportes

## Ejecución de Pruebas

```bash
# Ejecutar todas las pruebas
./mvnw test

# Ejecutar solo pruebas de HU1
./mvnw test -Dtest=HU1**

# Ejecutar solo pruebas de HU2
./mvnw test -Dtest=HU2**

# Ver cobertura con JaCoCo
./mvnw clean test jacoco:report
```

## Requisitos Cumplidos

✅ 1 prueba de Caja Blanca con complejidad ciclomática > 3 por HU
✅ 1 prueba de Caja Negra con > 4 campos por HU  
✅ 1 método con 4 pruebas unitarias por HU
