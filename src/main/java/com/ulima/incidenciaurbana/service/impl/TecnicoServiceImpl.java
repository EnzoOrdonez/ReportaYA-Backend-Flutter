package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.CompletarReporteRequest;
import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.dto.TecnicoDTO;
import com.ulima.incidenciaurbana.model.Asignacion;
import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.Foto;
import com.ulima.incidenciaurbana.model.Reporte;
import com.ulima.incidenciaurbana.model.Tecnico;
import com.ulima.incidenciaurbana.model.TipoFoto;
import com.ulima.incidenciaurbana.repository.AsignacionRepository;
import com.ulima.incidenciaurbana.repository.FotoRepository;
import com.ulima.incidenciaurbana.repository.ReporteRepository;
import com.ulima.incidenciaurbana.repository.TecnicoRepository;
import com.ulima.incidenciaurbana.service.ITecnicoService;
import com.ulima.incidenciaurbana.service.IReporteService;
import com.ulima.incidenciaurbana.service.IFirebaseStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TecnicoServiceImpl implements ITecnicoService {

    private final AsignacionRepository asignacionRepository;
    private final ReporteRepository reporteRepository;
    private final FotoRepository fotoRepository;
    private final IReporteService reporteService;
    private final TecnicoRepository tecnicoRepository;
    private final IFirebaseStorageService firebaseStorageService;
    private static final int PAGE_SIZE = 20;

    @Autowired
    public TecnicoServiceImpl(AsignacionRepository asignacionRepository,
            ReporteRepository reporteRepository,
            FotoRepository fotoRepository,
            IReporteService reporteService,
            TecnicoRepository tecnicoRepository,
            IFirebaseStorageService firebaseStorageService) {
        this.asignacionRepository = asignacionRepository;
        this.reporteRepository = reporteRepository;
        this.fotoRepository = fotoRepository;
        this.reporteService = reporteService;
        this.tecnicoRepository = tecnicoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Override
    public Page<TecnicoDTO> obtenerTodosTecnicos(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        Page<Tecnico> tecnicos = tecnicoRepository.findAll(pageable);

        return tecnicos.map(tecnico -> new TecnicoDTO(
                tecnico.getId(),
                tecnico.getUsuario(),
                tecnico.getPersona().getNombres(),
                tecnico.getPersona().getApellidos(),
                tecnico.getPersona().getCorreo(),
                null // Sin especialidad por ahora
        ));
    }

    @Override
    public Page<ReporteDTO> obtenerReportesAsignados(Long tecnicoId, String estado, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());

        if (estado == null || estado.trim().isEmpty()) {
            return new PageImpl<>(Objects.requireNonNull(Collections.<ReporteDTO>emptyList()), pageable, 0L);
        }

        try {
            EstadoReporte estadoEnum = EstadoReporte.valueOf(estado.toUpperCase());
            Page<Asignacion> asignaciones = asignacionRepository.findByTecnicoAndEstado(
                    tecnicoId,
                    estadoEnum,
                    pageable);

            // Convertir a DTO de forma segura sin lazy loading
            List<ReporteDTO> reportesDTO = asignaciones.getContent()
                    .stream()
                    .map(asignacion -> asignacion != null && asignacion.getReporte() != null
                            ? convertirADTO(asignacion.getReporte())
                            : null)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());

            return new PageImpl<>(Objects.requireNonNull(reportesDTO), pageable, asignaciones.getTotalElements());
        } catch (IllegalArgumentException e) {
            // Estado inválido
            return new PageImpl<>(Objects.requireNonNull(Collections.<ReporteDTO>emptyList()), pageable, 0L);
        }
    }

    @Override
    @Transactional
    public ReporteDTO completarReporte(Long tecnicoId, Long reporteId, CompletarReporteRequest request) {
        // VALIDACIÓN 0: Identificador de reporte no nulo
        Objects.requireNonNull(reporteId, "reporteId no puede ser null");

        // VALIDACIÓN 1: Reporte existe
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        // VALIDACIÓN 2: Reporte está en estado PROCESO o RECHAZADO_AUDITO
        // Permite tanto resolución inicial como reintento después de rechazo
        if (reporte.getEstado() != EstadoReporte.PROCESO &&
                reporte.getEstado() != EstadoReporte.RECHAZADO_AUDITO) {
            throw new IllegalArgumentException(
                    "El reporte debe estar en estado PROCESO o RECHAZADO_AUDITO para completarlo. Estado actual: "
                            + reporte.getEstado());
        }

        // VALIDACIÓN 3: Técnico existe y es asignado al reporte
        // Busca asignación en cualquiera de los dos estados (PROCESO o
        // RECHAZADO_AUDITO)
        Optional<Asignacion> asignacionOptional = asignacionRepository
                .findByReporteAndEstado(reporte, EstadoReporte.PROCESO)
                .stream()
                .findFirst();

        if (!asignacionOptional.isPresent()) {
            asignacionOptional = asignacionRepository.findByReporteAndEstado(reporte, EstadoReporte.RECHAZADO_AUDITO)
                    .stream()
                    .findFirst();
        }

        Asignacion asignacion = asignacionOptional
                .orElseThrow(() -> new IllegalArgumentException("No hay asignación activa para este reporte"));

        if (asignacion.getTecnico() == null || !Objects.equals(asignacion.getTecnico().getId(), tecnicoId)) {
            throw new IllegalArgumentException("Solo el técnico asignado puede completar el reporte");
        }

        // PASO 1: Guardar fotos en Firebase Storage
        try {
            for (CompletarReporteRequest.FotoRequestInline fotoRequest : request.getFotos()) {
                // Decodificar base64
                byte[] decodedBytes = Base64.getDecoder().decode(fotoRequest.getArchivoBase64());

                // Determinar extensión
                String extension = determinarExtensionFoto(decodedBytes);
                String nombreArchivo = "fotos/reporte-" + reporteId + "-" + fotoRequest.getTipo() + "-"
                        + UUID.randomUUID() + "." + extension;

                // Subir a Firebase Storage
                String urlFoto;
                if (firebaseStorageService.isAvailable()) {
                    // Si Firebase está disponible, subir a la nube
                    urlFoto = firebaseStorageService.uploadFile(nombreArchivo, decodedBytes);
                    System.out.println("Foto subida a Firebase Storage: " + nombreArchivo);
                } else {
                    // Fallback: guardar localmente si Firebase no está disponible
                    Path dirPath = Paths.get("uploads/fotos");
                    Files.createDirectories(dirPath);
                    Path filePath = dirPath.resolve(nombreArchivo);
                    Files.write(filePath, decodedBytes);
                    urlFoto = "uploads/fotos/" + nombreArchivo;
                    System.out.println("Firebase no disponible. Foto guardada localmente: " + nombreArchivo);
                }

                // Crear entidad Foto
                Foto foto = new Foto();
                foto.setReporte(reporte);
                foto.setUrl(urlFoto);
                foto.setTipo(TipoFoto.valueOf(fotoRequest.getTipo()));
                foto.setDescripcion(fotoRequest.getDescripcion());
                foto.setFechaCarga(LocalDateTime.now());

                // Guardar en BD
                fotoRepository.save(foto);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar fotos: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de foto inválido: " + e.getMessage());
        }

        // PASO 2: Actualizar comentario de resolución
        reporte.setComentarioResolucion(request.getComentarioResolucion());
        reporte = reporteRepository.save(reporte);

        // PASO 3: Cambiar estado a RESUELTA (delegando a ReporteService)
        // Esto garantiza que se registre en HistorialEstado y se envíe notificación
        reporteService.cambiarEstadoReporte(reporte.getId(), EstadoReporte.RESUELTA);

        // Recargar reporte actualizado
        reporte = reporteRepository.findById(reporte.getId())
                .orElseThrow(() -> new RuntimeException("Error al recargar reporte"));

        return convertirADTO(reporte);
    }

    private ReporteDTO convertirADTO(Reporte reporte) {
        if (reporte == null) {
            return null;
        }

        ReporteDTO dto = new ReporteDTO();
        dto.setId(reporte.getId());
        dto.setTitulo(reporte.getTitulo());
        dto.setDescripcion(reporte.getDescripcion());
        dto.setPrioridad(reporte.getPrioridad());
        dto.setEstado(reporte.getEstado());
        dto.setFechaCreacion(reporte.getFechaCreacion());
        dto.setFechaActualizacion(reporte.getFechaActualizacion());

        // Proteger contra nulls en relaciones
        try {
            if (reporte.getCuenta() != null) {
                dto.setCuentaId(reporte.getCuenta().getId());
                if (reporte.getCuenta().getPersona() != null) {
                    dto.setNombreCiudadano(reporte.getCuenta().getPersona().getNombres());
                }
            }
        } catch (Exception e) {
            // Si hay error en lazy loading de Cuenta, continuar sin ella
            e.printStackTrace();
        }

        // NO acceder a ubicacion para evitar lazy loading duplicates
        // La ubicacion se obtiene en un endpoint separado si es necesario

        return dto;
    }

    /**
     * Determina la extensión de archivo basado en los bytes iniciales (magic
     * numbers)
     */
    private String determinarExtensionFoto(byte[] bytes) {
        if (bytes.length < 4) {
            return "jpg"; // default
        }

        // Verificar PNG
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return "png";
        }

        // Verificar JPEG
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
            return "jpg";
        }

        // Verificar WebP
        if (bytes[0] == 0x52 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x46) {
            if (bytes.length > 12 && bytes[8] == 0x57 && bytes[9] == 0x45 && bytes[10] == 0x42 && bytes[11] == 0x50) {
                return "webp";
            }
        }

        return "jpg"; // default
    }
}
