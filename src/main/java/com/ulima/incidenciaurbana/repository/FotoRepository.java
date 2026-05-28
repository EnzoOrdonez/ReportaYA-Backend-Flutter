package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

    /**
     * Encuentra todas las fotos de un reporte específico
     */
    List<Foto> findByReporteId(Long reporteId);

    /**
     * Cuenta el número de fotos adjuntas a un reporte
     */
    long countByReporteId(Long reporteId);

    /**
     * Elimina todas las fotos de un reporte
     */
    void deleteByReporteId(Long reporteId);
}
