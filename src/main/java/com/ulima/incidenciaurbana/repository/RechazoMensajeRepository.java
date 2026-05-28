package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.RechazoMensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RechazoMensajeRepository extends JpaRepository<RechazoMensaje, Long> {
}
