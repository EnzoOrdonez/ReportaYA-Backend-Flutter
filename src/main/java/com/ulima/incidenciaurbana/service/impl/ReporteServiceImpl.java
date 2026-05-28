package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.dto.UbicacionDTO;
import com.ulima.incidenciaurbana.dto.FotoDTO;
import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.Prioridad;
import com.ulima.incidenciaurbana.model.Reporte;
import com.ulima.incidenciaurbana.model.Ubicacion;
import com.ulima.incidenciaurbana.model.Foto;
import com.ulima.incidenciaurbana.model.Asignacion;
import com.ulima.incidenciaurbana.model.Tecnico;
import com.ulima.incidenciaurbana.model.TipoFoto;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;
import com.ulima.incidenciaurbana.repository.CuentaRepository;
import com.ulima.incidenciaurbana.repository.ReporteRepository;
import com.ulima.incidenciaurbana.repository.UbicacionRepository;
import com.ulima.incidenciaurbana.service.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.ulima.incidenciaurbana.model.HistorialEstado;
import com.ulima.incidenciaurbana.model.MensajeNotificacion;
import com.ulima.incidenciaurbana.model.RechazoMensaje;
import com.ulima.incidenciaurbana.repository.HistorialEstadoRepository;
import com.ulima.incidenciaurbana.repository.MensajeNotificacionRepository;
import com.ulima.incidenciaurbana.repository.RechazoMensajeRepository;
import com.ulima.incidenciaurbana.service.INotificationService;

@Service
@Transactional
public class ReporteServiceImpl implements IReporteService {

    private final ReporteRepository reporteRepository;
    private final CuentaRepository cuentaRepository;
    private final UbicacionRepository ubicacionRepository;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final INotificationService notificationService;
    private final RechazoMensajeRepository rechazoMensajeRepository;
    private final MensajeNotificacionRepository mensajeNotificacionRepository;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public ReporteServiceImpl(ReporteRepository reporteRepository,
            CuentaRepository cuentaRepository,
            UbicacionRepository ubicacionRepository,
            HistorialEstadoRepository historialEstadoRepository,
            INotificationService notificationService,
            RechazoMensajeRepository rechazoMensajeRepository,
            MensajeNotificacionRepository mensajeNotificacionRepository) {
        this.reporteRepository = reporteRepository;
        this.cuentaRepository = cuentaRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.historialEstadoRepository = historialEstadoRepository;
        this.notificationService = notificationService;
        this.rechazoMensajeRepository = rechazoMensajeRepository;
        this.mensajeNotificacionRepository = mensajeNotificacionRepository;
    }

    @Override
    public ReporteDTO crearReporte(ReporteDTO reporteDTO) {
        // Validar que la ubicación sea obligatoria
        if (reporteDTO.getUbicacion() == null) {
            throw new RuntimeException("La ubicación es obligatoria para crear un reporte");
        }

        // Validar que la ubicación tenga latitud y longitud
        if (reporteDTO.getUbicacion().getLatitud() == null || reporteDTO.getUbicacion().getLongitud() == null) {
            throw new RuntimeException("La latitud y longitud son obligatorias en la ubicación");
        }

        if (reporteDTO.getCuentaId() == null) {
            throw new RuntimeException("El ID de la cuenta es obligatorio");
        }
        long cuentaId = reporteDTO.getCuentaId();
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + cuentaId));

        Reporte reporte = new Reporte(
                reporteDTO.getTitulo(),
                reporteDTO.getDescripcion(),
                cuenta);

        if (reporteDTO.getPrioridad() != null) {
            reporte.setPrioridad(reporteDTO.getPrioridad());
        }

        if (reporteDTO.getTipoProblema() != null) {
            reporte.setTipoProblema(reporteDTO.getTipoProblema());
        }

        // Crear y asociar ubicación (ahora es obligatoria)
        Ubicacion ubicacion = convertirDTOAUbicacion(reporteDTO.getUbicacion());
        if (ubicacion == null) {
            throw new RuntimeException("Error al crear la ubicación");
        }
        ubicacion = ubicacionRepository.save(ubicacion);
        reporte.setUbicacion(ubicacion);

        cuenta.crearReporte(reporte);
        reporte = reporteRepository.save(reporte);

        // Guardar historial inicial
        HistorialEstado historial = new HistorialEstado(reporte, null, reporte.getEstado());
        historialEstadoRepository.save(historial);

        return convertirADTO(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteDTO obtenerReportePorId(Long id) {
        if (id == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        return convertirADTO(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> obtenerTodosReportes(int page, EstadoReporte estado) {
        int p = Math.max(0, page);
        PageRequest pageRequest = PageRequest.of(p, PAGE_SIZE, Sort.by("fechaCreacion").descending());

        if (estado != null) {
            return reporteRepository.findByEstado(estado, pageRequest)
                    .map(this::convertirADTO);
        }

        return reporteRepository.findAll(pageRequest)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> obtenerReportesPorCuenta(Long cuentaId, int page) {
        int p = Math.max(0, page);
        return reporteRepository
                .findByCuentaId(cuentaId, PageRequest.of(p, PAGE_SIZE, Sort.by("fechaCreacion").descending()))
                .map(this::convertirADTO);
    }

    @Override
    public ReporteDTO actualizarReporte(Long id, ReporteDTO reporteDTO) {
        if (id == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        long reporteId = id;
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + reporteId));

        reporte.setTitulo(reporteDTO.getTitulo());
        reporte.setDescripcion(reporteDTO.getDescripcion());

        if (reporteDTO.getPrioridad() != null) {
            reporte.setPrioridad(reporteDTO.getPrioridad());
        }

        if (reporteDTO.getTipoProblema() != null) {
            reporte.setTipoProblema(reporteDTO.getTipoProblema());
        }

        // La ubicación es obligatoria, debe estar presente
        if (reporteDTO.getUbicacion() == null) {
            throw new RuntimeException("La ubicación es obligatoria. No se puede actualizar un reporte sin ubicación");
        }

        // Validar que la ubicación tenga latitud y longitud
        if (reporteDTO.getUbicacion().getLatitud() == null || reporteDTO.getUbicacion().getLongitud() == null) {
            throw new RuntimeException("La latitud y longitud son obligatorias en la ubicación");
        }

        // Actualizar ubicación existente
        Ubicacion ubicacion = reporte.getUbicacion();
        ubicacion.setLatitud(reporteDTO.getUbicacion().getLatitud());
        ubicacion.setLongitud(reporteDTO.getUbicacion().getLongitud());
        ubicacion.setDireccion(reporteDTO.getUbicacion().getDireccion());
        ubicacionRepository.save(ubicacion);

        reporte = reporteRepository.save(reporte);
        return convertirADTO(reporte);
    }

    @Override
    public ReporteDTO cambiarEstadoReporte(Long id, EstadoReporte nuevoEstado) {
        if (id == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        long reporteId = id;
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + reporteId));

        EstadoReporte estadoAnterior = reporte.getEstado();

        // Si el estado no cambia, no hacemos nada
        if (estadoAnterior == nuevoEstado) {
            return convertirADTO(reporte);
        }

        reporte.cambiarEstado(nuevoEstado);
        reporte = reporteRepository.save(reporte);

        // Guardar historial
        HistorialEstado historial = new HistorialEstado(reporte, estadoAnterior, nuevoEstado);
        historialEstadoRepository.save(historial);

        // Enviar notificación
        String mensaje = "El estado de tu reporte '" + reporte.getTitulo() + "' ha cambiado a " + nuevoEstado;

        // Buscar mensaje personalizado
        MensajeNotificacion mensajeNotificacion = mensajeNotificacionRepository.findByEstado(nuevoEstado).orElse(null);
        if (mensajeNotificacion != null) {
            mensaje = mensajeNotificacion.getMensaje();
        }

        notificationService.enviarNotificacion(reporte.getCuenta().getId(), "Actualización de Reporte", mensaje);

        return convertirADTO(reporte);
    }

    @Override
    public ReporteDTO rechazarReporte(Long id, String motivo) {
        if (id == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        long reporteId = id;
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + reporteId));

        EstadoReporte estadoAnterior = reporte.getEstado();
        EstadoReporte nuevoEstado = EstadoReporte.RECHAZADO;

        if (estadoAnterior == nuevoEstado) {
            return convertirADTO(reporte);
        }

        reporte.cambiarEstado(nuevoEstado);
        reporte = reporteRepository.save(reporte);

        // Guardar historial
        HistorialEstado historial = new HistorialEstado(reporte, estadoAnterior, nuevoEstado);
        historialEstadoRepository.save(historial);

        // Guardar motivo de rechazo
        RechazoMensaje rechazo = new RechazoMensaje(reporte, motivo);
        rechazoMensajeRepository.save(rechazo);

        // Enviar notificación
        String mensaje = "Tu reporte ha sido rechazado. Motivo: " + motivo;

        // Buscar mensaje personalizado
        MensajeNotificacion mensajeNotificacion = mensajeNotificacionRepository.findByEstado(nuevoEstado).orElse(null);
        if (mensajeNotificacion != null) {
            mensaje = mensajeNotificacion.getMensaje() + ". Motivo: " + motivo;
        }

        notificationService.enviarNotificacion(reporte.getCuenta().getId(), "Reporte Rechazado", mensaje);

        return convertirADTO(reporte);
    }

    @Override
    public ReporteDTO cambiarPrioridadReporte(Long id, Prioridad nuevaPrioridad) {
        if (id == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        long reporteId = id;
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + reporteId));

        reporte.cambiarPrioridad(nuevaPrioridad);
        reporte = reporteRepository.save(reporte);
        return convertirADTO(reporte);
    }

    @Override
    public void eliminarReporte(Long id) {
        if (id == null) {
            throw new RuntimeException("El ID del reporte es obligatorio");
        }
        long reporteId = id;
        if (!reporteRepository.existsById(reporteId)) {
            throw new RuntimeException("Reporte no encontrado con id: " + reporteId);
        }
        reporteRepository.deleteById(reporteId);
    }

    // Operator-specific paginated view removed; operators use
    // obtenerTodosReportes(page)

    private ReporteDTO convertirADTO(Reporte reporte) {
        ReporteDTO dto = new ReporteDTO(
                reporte.getId(),
                reporte.getTitulo(),
                reporte.getDescripcion(),
                reporte.getCuenta().getId(),
                reporte.getCuenta().getPersona().getNombreCompleto(),
                reporte.getPrioridad(),
                reporte.getEstado(),
                reporte.getTipoProblema());

        dto.setFechaCreacion(reporte.getFechaCreacion());
        dto.setFechaActualizacion(reporte.getFechaActualizacion());

        // Convertir ubicación si existe
        if (reporte.getUbicacion() != null) {
            dto.setUbicacion(convertirUbicacionADTO(reporte.getUbicacion()));
        }

        // Mapear fotos con base64 - filtrar solo las últimas 3 (una de cada tipo:
        // INICIAL, PROCESO, FINAL)
        if (reporte.getFotos() != null && !reporte.getFotos().isEmpty()) {
            List<Foto> fotosFiltradasYOrdenadas = reporte.getFotos().stream()
                    // Agrupar por tipo de foto y tomar la más reciente de cada tipo
                    .collect(Collectors.groupingBy(
                            Foto::getTipo,
                            Collectors.maxBy((f1, f2) -> f1.getFechaCarga().compareTo(f2.getFechaCarga()))))
                    .values()
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    // Ordenar por tipo: INICIAL, PROCESO, FINAL
                    .sorted((f1, f2) -> {
                        Map<TipoFoto, Integer> orden = Map.of(
                                TipoFoto.INICIAL, 1,
                                TipoFoto.PROCESO, 2,
                                TipoFoto.FINAL, 3);
                        return orden.getOrDefault(f1.getTipo(), 99).compareTo(orden.getOrDefault(f2.getTipo(), 99));
                    })
                    .toList();

            dto.setFotos(fotosFiltradasYOrdenadas.stream()
                    .map(this::convertirFotoADTO)
                    .toList());
        }

        // Mapear comentarioResolucion
        dto.setComentarioResolucion(reporte.getComentarioResolucion());

        // Mapear técnico asignado (obtener la asignación más reciente)
        if (reporte.getAsignaciones() != null && !reporte.getAsignaciones().isEmpty()) {
            // Obtener la última asignación (la más reciente)
            Asignacion ultimaAsignacion = reporte.getAsignaciones().stream()
                    .max((a, b) -> a.getFechaAsignacion().compareTo(b.getFechaAsignacion()))
                    .orElse(null);

            if (ultimaAsignacion != null && ultimaAsignacion.getTecnico() != null) {
                Tecnico tecnico = ultimaAsignacion.getTecnico();
                dto.setTecnicoId(tecnico.getId());
                dto.setTecnicoNombre(tecnico.getPersona().getNombreCompleto());
            }
        }

        // Mapear contador de rechazos
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

    private FotoDTO convertirFotoADTO(Foto foto) {
        FotoDTO fotoDTO = new FotoDTO();
        fotoDTO.setId(foto.getId());
        fotoDTO.setReporteId(foto.getReporte().getId());
        fotoDTO.setUrl(foto.getUrl());
        fotoDTO.setTipo(foto.getTipo());
        fotoDTO.setDescripcion(foto.getDescripcion());
        fotoDTO.setFechaCarga(foto.getFechaCarga());

        // Convertir archivo a base64 si existe
        String base64 = archivoABase64(foto.getUrl());
        fotoDTO.setArchivoBase64(base64);

        return fotoDTO;
    }

    private String archivoABase64(String urlArchivo) {
        try {
            if (urlArchivo == null || urlArchivo.isEmpty()) {
                return null;
            }

            java.nio.file.Path rutaArchivo = java.nio.file.Paths.get(urlArchivo);
            if (!java.nio.file.Files.exists(rutaArchivo)) {
                System.err.println("Archivo no encontrado: " + urlArchivo);
                return null;
            }

            byte[] contenido = java.nio.file.Files.readAllBytes(rutaArchivo);
            return java.util.Base64.getEncoder().encodeToString(contenido);
        } catch (Exception e) {
            System.err.println("Error al convertir archivo a base64: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> obtenerReportesMapa(EstadoReporte estado,
            com.ulima.incidenciaurbana.model.TipoProblema tipo, Prioridad prioridad) {
        org.springframework.data.jpa.domain.Specification<Reporte> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipoProblema"), tipo));
            }
            if (prioridad != null) {
                predicates.add(cb.equal(root.get("prioridad"), prioridad));
            }

            // Fetch join para optimizar
            root.fetch("ubicacion", jakarta.persistence.criteria.JoinType.LEFT);
            root.fetch("cuenta", jakarta.persistence.criteria.JoinType.LEFT);

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return reporteRepository.findAll(spec).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private Ubicacion convertirDTOAUbicacion(UbicacionDTO dto) {
        Ubicacion ubicacion = new Ubicacion(
                dto.getLatitud(),
                dto.getLongitud(),
                dto.getDireccion());
        return ubicacion;
    }
}
