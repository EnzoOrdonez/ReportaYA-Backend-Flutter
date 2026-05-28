package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.TokenNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenNotificacionRepository extends JpaRepository<TokenNotificacion, Long> {
    Optional<TokenNotificacion> findByCuentaId(Long cuentaId);
}
