package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.AsignacionDTO;
import com.ulima.incidenciaurbana.model.Asignacion;
import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.OperadorMunicipal;
import com.ulima.incidenciaurbana.model.Reporte;
import com.ulima.incidenciaurbana.model.Tecnico;
import com.ulima.incidenciaurbana.repository.AsignacionRepository;
import com.ulima.incidenciaurbana.repository.CuentaRepository;
import com.ulima.incidenciaurbana.repository.ReporteRepository;
import com.ulima.incidenciaurbana.service.IAsignacionService;
import com.ulima.incidenciaurbana.service.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsignacionServiceImpl implements IAsignacionService {

        private final AsignacionRepository asignacionRepository;
        private final ReporteRepository reporteRepository;
        private final CuentaRepository cuentaRepository;
        private final IReporteService reporteService;

        @Autowired
        public AsignacionServiceImpl(AsignacionRepository asignacionRepository,
                        ReporteRepository reporteRepository,
                        CuentaRepository cuentaRepository,
                        IReporteService reporteService) {
                this.asignacionRepository = asignacionRepository;
                this.reporteRepository = reporteRepository;
                this.cuentaRepository = cuentaRepository;
                this.reporteService = reporteService;
        }

        @Override
        public AsignacionDTO crearAsignacion(AsignacionDTO asignacionDTO) {
                // ✅ VALIDACIÓN 1: Prioridad obligatoria
                if (asignacionDTO.getPrioridad() == null) {
                        throw new RuntimeException(
                                        " La prioridad es obligatoria. Selecciona BAJA, MEDIA o ALTA antes de asignar.");
                }

                // ✅ VALIDACIÓN 2: Reporte existe
                Reporte reporte = reporteRepository.findById(asignacionDTO.getReporteId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Reporte no encontrado con id: " + asignacionDTO.getReporteId()));

                // ✅ VALIDACIÓN 3: Validar que el reporte esté en estado REVISION
                if (reporte.getEstado() != EstadoReporte.REVISION) {
                        throw new RuntimeException(
                                        "El reporte debe estar en estado REVISION para poder asignar un técnico. Estado actual: "
                                                        + reporte.getEstado());
                }

                // ✅ VALIDACIÓN 4: Operador existe y es válido
                Cuenta operadorCuenta = cuentaRepository.findById(asignacionDTO.getOperadorId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Operador no encontrado con id: " + asignacionDTO.getOperadorId()));

                if (!(operadorCuenta instanceof OperadorMunicipal)) {
                        throw new RuntimeException(
                                        "El usuario con id " + asignacionDTO.getOperadorId()
                                                        + " no es un operador municipal");
                }

                OperadorMunicipal operador = (OperadorMunicipal) operadorCuenta;

                // ✅ VALIDACIÓN 5: Técnico existe y es válido
                Cuenta tecnicoCuenta = cuentaRepository.findById(asignacionDTO.getTecnicoId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Técnico no encontrado con id: " + asignacionDTO.getTecnicoId()));

                if (!(tecnicoCuenta instanceof Tecnico)) {
                        throw new RuntimeException(
                                        "El usuario con id " + asignacionDTO.getTecnicoId() + " no es un técnico");
                }

                Tecnico tecnico = (Tecnico) tecnicoCuenta;

                // ✅ PASO 1: Actualizar prioridad del reporte
                reporte.setPrioridad(asignacionDTO.getPrioridad());
                reporteRepository.save(reporte);

                // ✅ PASO 2: Cerrar asignación previa si existe (solo puede haber una asignación
                // activa
                // por reporte)
                asignacionRepository.findByReporteIdAndFechaCierreIsNull(asignacionDTO.getReporteId())
                                .ifPresent(asignacionPrevia -> {
                                        asignacionPrevia.setFechaCierre(java.time.LocalDateTime.now());
                                        asignacionRepository.save(asignacionPrevia);
                                });

                // ✅ PASO 3: Crear la nueva asignación usando el modelo
                Asignacion asignacion = operador.asignarTecnico(reporte, tecnico);
                asignacion = asignacionRepository.save(asignacion);

                // ✅ PASO 4: Actualizar estado del reporte a PROCESO (delegando a
                // ReporteService)
                // Esto garantiza que se registre en HistorialEstado y se envíe notificación
                reporteService.cambiarEstadoReporte(reporte.getId(), EstadoReporte.PROCESO);

                // Agregar la asignación al reporte
                reporte.agregarAsignacion(asignacion);
                reporteRepository.save(reporte);

                return convertirADTO(asignacion);
        }

        /**
         * Convierte una entidad Asignacion a AsignacionDTO
         */
        private AsignacionDTO convertirADTO(Asignacion asignacion) {
                return new AsignacionDTO(
                                asignacion.getId(),
                                asignacion.getReporte().getId(),
                                asignacion.getOperador().getId(),
                                asignacion.getTecnico().getId(),
                                asignacion.getReporte().getPrioridad(),
                                asignacion.getReporte().getTitulo(),
                                asignacion.getOperador().getPersona().getNombreCompleto(),
                                asignacion.getTecnico().getPersona().getNombreCompleto(),
                                asignacion.getFechaAsignacion(),
                                asignacion.getFechaCierre());
        }
}
