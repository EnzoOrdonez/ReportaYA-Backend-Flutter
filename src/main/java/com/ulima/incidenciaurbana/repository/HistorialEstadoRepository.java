package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findByReporteIdOrderByFechaCambioDesc(Long reporteId);
}
