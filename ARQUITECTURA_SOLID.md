# Sistema ReportaYA - Backend API REST



## 🌐 API REST Endpoints Creados

### � Ciudadanos
```
POST   /api/ciudadanos                    - Crear ciudadano
GET    /api/ciudadanos                    - Listar todos
GET    /api/ciudadanos/{id}               - Obtener por ID
GET    /api/ciudadanos/dni/{dni}          - Buscar por DNI
PUT    /api/ciudadanos/{id}               - Actualizar
DELETE /api/ciudadanos/{id}               - Eliminar
```

### �📋 Reportes
```
POST   /api/reportes                      - Crear reporte (requiere cuentaId)
GET    /api/reportes                      - Listar todos
GET    /api/reportes/{id}                 - Obtener por ID
GET    /api/reportes/cuenta/{cuentaId}    - Reportes de un ciudadano
GET    /api/reportes/estado/{estado}      - Reportes por estado
PUT    /api/reportes/{id}                 - Actualizar
PATCH  /api/reportes/{id}/estado          - Cambiar estado
PATCH  /api/reportes/{id}/prioridad       - Cambiar prioridad
DELETE /api/reportes/{id}                 - Eliminar
```
