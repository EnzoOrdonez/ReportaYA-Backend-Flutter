package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.MensajeNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MensajeNotificacionRepository extends JpaRepository<MensajeNotificacion, Long> {
    Optional<MensajeNotificacion> findByEstado(EstadoReporte estado);
}
