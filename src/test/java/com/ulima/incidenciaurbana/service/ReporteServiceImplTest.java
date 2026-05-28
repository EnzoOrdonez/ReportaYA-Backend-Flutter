package com.ulima.incidenciaurbana.service;

import com.ulima.incidenciaurbana.dto.ReporteDTO;
import com.ulima.incidenciaurbana.model.*;
import com.ulima.incidenciaurbana.repository.*;
import com.ulima.incidenciaurbana.service.impl.ReporteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReporteServiceImplTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Mock
    private HistorialEstadoRepository historialEstadoRepository;

    @Mock
    private INotificationService notificationService;

    @Mock
    private RechazoMensajeRepository rechazoMensajeRepository;

    @Mock
    private MensajeNotificacionRepository mensajeNotificacionRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Reporte reporte;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cuenta = new Ciudadano(); // Usar una clase concreta
        cuenta.setId(1L);
        cuenta.setPersona(new Persona()); // Evitar NullPointerException en convertirADTO

        reporte = new Reporte("Bache en la pista", "Hueco grande", cuenta);
        reporte.setId(100L);
        reporte.setEstado(EstadoReporte.PENDIENTE);
    }

    // Prueba 1: Cambio de estado exitoso (PENDIENTE -> REVISION)
    @Test
    void cambiarEstadoReporte_Exito() {
        // Arrange
        when(reporteRepository.findById(100L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mensajeNotificacionRepository.findByEstado(EstadoReporte.REVISION)).thenReturn(Optional.empty());

        // Act
        ReporteDTO resultado = reporteService.cambiarEstadoReporte(100L, EstadoReporte.REVISION);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoReporte.REVISION, resultado.getEstado());
        verify(reporteRepository).save(reporte);
        verify(historialEstadoRepository).save(any(HistorialEstado.class));
        verify(notificationService).enviarNotificacion(eq(1L), anyString(), anyString());
    }

    // Prueba 2: Cambio al mismo estado (No debe hacer nada)
    @Test
    void cambiarEstadoReporte_MismoEstado() {
        // Arrange
        when(reporteRepository.findById(100L)).thenReturn(Optional.of(reporte));

        // Act
        ReporteDTO resultado = reporteService.cambiarEstadoReporte(100L, EstadoReporte.PENDIENTE);

        // Assert
        assertEquals(EstadoReporte.PENDIENTE, resultado.getEstado());
        verify(reporteRepository, never()).save(any(Reporte.class));
        verify(notificationService, never()).enviarNotificacion(anyLong(), anyString(), anyString());
    }

    // Prueba 3: Reporte no encontrado (Debe lanzar excepción)
    @Test
    void cambiarEstadoReporte_ReporteNoEncontrado() {
        // Arrange
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reporteService.cambiarEstadoReporte(999L, EstadoReporte.REVISION);
        });

        assertEquals("Reporte no encontrado con id: 999", exception.getMessage());
    }

    // Prueba 4: Verificación de mensaje personalizado en notificación
    @Test
    void cambiarEstadoReporte_ConMensajePersonalizado() {
        // Arrange
        when(reporteRepository.findById(100L)).thenReturn(Optional.of(reporte));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MensajeNotificacion mensajePersonalizado = new MensajeNotificacion(EstadoReporte.RESUELTA, "Tu reporte ha sido resuelto con éxito.");
        when(mensajeNotificacionRepository.findByEstado(EstadoReporte.RESUELTA)).thenReturn(Optional.of(mensajePersonalizado));

        // Act
        reporteService.cambiarEstadoReporte(100L, EstadoReporte.RESUELTA);

        // Assert
        verify(notificationService).enviarNotificacion(eq(1L), eq("Actualización de Reporte"), eq("Tu reporte ha sido resuelto con éxito."));
    }
}
