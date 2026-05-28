package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.CuentaDTO;
import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.dto.UbicacionDTO;
import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.OperadorMunicipal;
import com.ulima.incidenciaurbana.model.Reporte;
import com.ulima.incidenciaurbana.model.Ubicacion;
import com.ulima.incidenciaurbana.repository.CuentaRepository;
import com.ulima.incidenciaurbana.repository.FotoRepository;
import com.ulima.incidenciaurbana.repository.ReporteRepository;
import com.ulima.incidenciaurbana.service.IOperadorService;
import com.ulima.incidenciaurbana.service.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class OperadorServiceImpl implements IOperadorService {

    private final ReporteRepository reporteRepository;
    private final CuentaRepository cuentaRepository;
    private final FotoRepository fotoRepository;
    private final IReporteService reporteService;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public OperadorServiceImpl(ReporteRepository reporteRepository,
            CuentaRepository cuentaRepository,
            FotoRepository fotoRepository,
            IReporteService reporteService) {
        this.reporteRepository = reporteRepository;
        this.cuentaRepository = cuentaRepository;
        this.fotoRepository = fotoRepository;
        this.reporteService = reporteService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> obtenerReportesParaAuditoria(String estado, int page) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es requerido");
        }

        try {
            EstadoReporte estadoEnum = EstadoReporte.valueOf(estado.toUpperCase());
            int p = Math.max(0, page);

            // Usar la query con JOIN FETCH para cargar todas las relaciones
            List<Reporte> todosReportes = reporteRepository.findByEstadoWithDetails(estadoEnum);

            // Calcular paginación manualmente
            int start = p * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, todosReportes.size());

            List<ReporteDTO> reportesPaginados = todosReportes.subList(start, end)
                    .stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            return new PageImpl<>(Objects.requireNonNull(reportesPaginados),
                    PageRequest.of(p, PAGE_SIZE),
                    todosReportes.size());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + estado);
        }
    }

    @Override
    @Transactional
    public ReporteDTO cerrarReporte(Long reporteId, Long operadorId, String comentarioCierre) {
        if (reporteId == null || operadorId == null) {
            throw new IllegalArgumentException("reporteId y operadorId son requeridos");
        }

        // IMPORTANTE: Usar findByIdWithDetails para cargar relaciones y evitar
        // lazy-loading
        Reporte reporte = reporteRepository.findByIdWithDetails(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + reporteId));

        // Validación 1: Estado debe ser RESUELTA
        if (!reporte.getEstado().equals(EstadoReporte.RESUELTA)) {
            throw new RuntimeException(
                    "El reporte debe estar en estado RESUELTA para cerrar. Estado actual: " + reporte.getEstado());
        }

        Cuenta operador = cuentaRepository.findById(operadorId)
                .orElseThrow(() -> new RuntimeException("Operador no encontrado con id: " + operadorId));

        // Validación 2: Validar que sea operador municipal
        if (!(operador instanceof OperadorMunicipal)) {
            throw new RuntimeException("Solo operadores municipales pueden cerrar reportes");
        }

        // Validación 3: Validar que tenga asignación (fotos fueron obligatorias)
        if (reporte.getAsignaciones() == null || reporte.getAsignaciones().isEmpty()) {
            throw new RuntimeException("El reporte no tiene asignación de técnico");
        }

        // Validación 4: Reporte TIENE FOTOS (fueron obligatorias)
        long cantidadFotos = fotoRepository.countByReporteId(reporteId);
        if (cantidadFotos == 0) {
            throw new RuntimeException("El reporte no tiene fotos adjuntas. No se puede cerrar sin evidencias.");
        }

        // Validación 5: Reporte TIENE COMENTARIO DE RESOLUCIÓN (fue obligatorio)
        if (reporte.getComentarioResolucion() == null || reporte.getComentarioResolucion().trim().isEmpty()) {
            throw new RuntimeException(
                    "El reporte no tiene comentario de resolución. No se puede cerrar sin comentario del técnico.");
        }

        // Actualizar campos de cierre
        reporte.setComentarioCierre(comentarioCierre);
        reporte.setFechaCierre(LocalDateTime.now());
        reporte.setOperadorCierre(operador);
        reporte = reporteRepository.save(reporte);

        // Cambiar estado a CERRADA (delegando a ReporteService)
        // Esto garantiza que se registre en HistorialEstado y se envíe notificación
        reporteService.cambiarEstadoReporte(reporteId, EstadoReporte.CERRADA);

        // Recargar reporte actualizado
        reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Error al recargar reporte"));
        return convertirADTO(reporte);
    }

    @Override
    @Transactional
    public ReporteDTO rechazarAudito(Long reporteId, Long operadorId, String comentarioRechazo) {
        if (reporteId == null || operadorId == null) {
            throw new IllegalArgumentException("reporteId y operadorId son requeridos");
        }

        // Validación: comentarioRechazo es OBLIGATORIO
        if (comentarioRechazo == null || comentarioRechazo.trim().isEmpty()) {
            throw new RuntimeException("El comentario de rechazo es OBLIGATORIO");
        }

        if (comentarioRechazo.length() < 10) {
            throw new RuntimeException("El comentario debe tener mínimo 10 caracteres");
        }

        if (comentarioRechazo.length() > 1000) {
            throw new RuntimeException("El comentario no puede exceder 1000 caracteres");
        }

        // IMPORTANTE: Usar findByIdWithDetails para cargar relaciones y evitar
        // lazy-loading
        Reporte reporte = reporteRepository.findByIdWithDetails(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + reporteId));

        // Validación 1: Estado debe ser RESUELTA
        if (!reporte.getEstado().equals(EstadoReporte.RESUELTA)) {
            throw new RuntimeException(
                    "El reporte debe estar en estado RESUELTA para rechazar. Estado actual: " + reporte.getEstado());
        }

        Cuenta operador = cuentaRepository.findById(operadorId)
                .orElseThrow(() -> new RuntimeException("Operador no encontrado con id: " + operadorId));

        // Validación 2: Validar que sea operador municipal
        if (!(operador instanceof OperadorMunicipal)) {
            throw new RuntimeException("Solo operadores municipales pueden rechazar reportes");
        }

        // Validación 3: Verificar si se alcanzó máximo rechazos
        Integer contadorRechazos = reporte.getContadorRechazos() != null ? reporte.getContadorRechazos() : 0;
        final int MAX_RECHAZOS = 3;

        // Actualizar campos de rechazo
        reporte.setComentarioRechazo(comentarioRechazo);
        reporte.setFechaRechazo(LocalDateTime.now());
        reporte.setOperadorRechazo(operador);

        // Determinar el estado según contador de rechazos
        EstadoReporte nuevoEstado;
        if (contadorRechazos >= MAX_RECHAZOS) {
            // CIERRE DEFINITIVO: Estado final sin reintentos
            nuevoEstado = EstadoReporte.RECHAZADO;
            // No incrementar contador (ya alcanzó máximo)
        } else {
            // RECHAZO TEMPORAL: Permite reintentos (RESUELTA → RECHAZADO_AUDITO)
            nuevoEstado = EstadoReporte.RECHAZADO_AUDITO;
            reporte.setContadorRechazos(contadorRechazos + 1);
        }

        reporte = reporteRepository.save(reporte);

        // Cambiar estado (delegando a ReporteService)
        // Esto garantiza que se registre en HistorialEstado y se envíe notificación
        reporteService.cambiarEstadoReporte(reporteId, nuevoEstado);

        // Recargar reporte actualizado
        reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Error al recargar reporte"));
        return convertirADTO(reporte);
    }

    // ========================================

    private ReporteDTO convertirADTO(Reporte reporte) {
        // Validación defensiva para evitar NullPointerException
        String nombreCiudadano = "Sin información";
        if (reporte.getCuenta() != null && reporte.getCuenta().getPersona() != null) {
            nombreCiudadano = reporte.getCuenta().getPersona().getNombreCompleto();
        }

        ReporteDTO dto = new ReporteDTO(
                reporte.getId(),
                reporte.getTitulo(),
                reporte.getDescripcion(),
                reporte.getCuenta() != null ? reporte.getCuenta().getId() : null,
                nombreCiudadano,
                reporte.getPrioridad(),
                reporte.getEstado(),
                reporte.getTipoProblema());

        dto.setFechaCreacion(reporte.getFechaCreacion());
        dto.setFechaActualizacion(reporte.getFechaActualizacion());

        // Convertir ubicación si existe
        if (reporte.getUbicacion() != null) {
            dto.setUbicacion(convertirUbicacionADTO(reporte.getUbicacion()));
        }

        // Mapear campos de auditoría
        dto.setComentarioCierre(reporte.getComentarioCierre());
        dto.setFechaCierre(reporte.getFechaCierre());
        if (reporte.getOperadorCierre() != null) {
            dto.setOperadorCierre(convertirCuentaADTO(reporte.getOperadorCierre()));
        }

        dto.setComentarioRechazo(reporte.getComentarioRechazo());
        dto.setFechaRechazo(reporte.getFechaRechazo());
        if (reporte.getOperadorRechazo() != null) {
            dto.setOperadorRechazo(convertirCuentaADTO(reporte.getOperadorRechazo()));
        }

        dto.setContadorRechazos(reporte.getContadorRechazos());

        return dto;
    }

    private UbicacionDTO convertirUbicacionADTO(Ubicacion ubicacion) {
        return new UbicacionDTO(
                ubicacion.getId(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                ubicacion.getDireccion(),
                ubicacion.getFechaRegistro());
    }

    private CuentaDTO convertirCuentaADTO(Cuenta cuenta) {
        CuentaDTO dto = new CuentaDTO();
        dto.setId(cuenta.getId());
        dto.setUsuario(cuenta.getUsuario());
        if (cuenta.getPersona() != null) {
            dto.setNombres(cuenta.getPersona().getNombres());
            dto.setApellidos(cuenta.getPersona().getApellidos());
        }
        return dto;
    }
}
