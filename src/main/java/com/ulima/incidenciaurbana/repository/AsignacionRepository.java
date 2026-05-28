package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.Asignacion;
import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    Optional<Asignacion> findByReporteIdAndFechaCierreIsNull(Long reporteId);

    List<Asignacion> findByReporteId(Long reporteId);

    @Query("SELECT a FROM Asignacion a WHERE a.tecnico.id = :tecnicoId AND a.reporte.estado = :estado ORDER BY a.id DESC")
    Page<Asignacion> findByTecnicoAndEstado(@Param("tecnicoId") Long tecnicoId, @Param("estado") EstadoReporte estado,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.tecnico.id = :tecnicoId AND a.reporte.estado = :estado")
    long countByTecnicoAndEstado(@Param("tecnicoId") Long tecnicoId, @Param("estado") EstadoReporte estado);

    /**
     * Encuentra asignaciones por reporte y estado
     * Usado para validar que el técnico es el asignado al reporte
     */
    @Query("SELECT a FROM Asignacion a WHERE a.reporte = :reporte AND a.reporte.estado = :estado")
    List<Asignacion> findByReporteAndEstado(@Param("reporte") Reporte reporte, @Param("estado") EstadoReporte estado);
}
