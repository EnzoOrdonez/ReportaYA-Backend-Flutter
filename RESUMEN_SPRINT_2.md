# Resumen del Sprint 2: Gestión de Reportes y Triaje

En este Sprint 2, el desarrollo se centró en la implementación del núcleo del negocio: la gestión de reportes (ciclo de vida), el módulo de triaje (asignaciones) y las notificaciones. A continuación, se describen los paquetes y clases principales creados o modificados:

### Paquete: com.ulima.incidenciaurbana.controller
- **ReporteController.java**: Expone los endpoints REST para la creación, filtrado, actualización y gestión del ciclo de vida de los reportes.
- **AsignacionController.java**: Controlador encargado de gestionar las solicitudes de asignación de técnicos a reportes en revisión.
- **NotificationController.java**: Maneja el registro de tokens de dispositivos y endpoints de prueba para notificaciones push.
- **HistorialEstadoController.java**: Provee acceso a la línea de tiempo de cambios de estado de los reportes para auditoría y seguimiento.

### Paquete: com.ulima.incidenciaurbana.service.impl
- **ReporteServiceImpl.java**: Contiene la lógica central del negocio para validaciones, transiciones de estado y coordinación de notificaciones.
- **AsignacionServiceImpl.java**: Implementa la lógica para validar y registrar la asignación de un técnico a una incidencia específica.
- **NotificationServiceImpl.java**: Integra Firebase Cloud Messaging (FCM) para el envío de alertas en tiempo real a los ciudadanos.

### Paquete: com.ulima.incidenciaurbana.model
- **Reporte.java**: Entidad principal que representa la incidencia, sus datos geográficos, evidencias y estado actual.
- **Asignacion.java**: Entidad que vincula un reporte con un técnico, registrando la fecha y estado de la asignación.
- **MensajeNotificacion.java**: Entidad para configurar mensajes personalizados asociados a cada estado del reporte.
- **EstadoReporte.java**: Enumeración que define los estados válidos del flujo de trabajo (PENDIENTE, REVISION, PROCESO, etc.).

### Paquete: com.ulima.incidenciaurbana.dto
- **ReporteDTO.java**: Objeto de transferencia que encapsula los datos del reporte para la comunicación entre cliente y servidor.
- **AsignacionDTO.java**: DTO utilizado para transferir los datos necesarios al realizar una asignación de técnico.
- **TokenNotificacionDTO.java**: Estructura para recibir y validar el token de notificación del dispositivo cliente.

### Paquete: com.ulima.incidenciaurbana.repository
- **ReporteRepository.java**: Interfaz de acceso a datos que incluye consultas personalizadas y uso de EntityGraphs para optimización.
- **MensajeNotificacionRepository.java**: Repositorio JPA para recuperar los mensajes de notificación configurados por estado.
