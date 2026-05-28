package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByUsuario(String usuario);
    Optional<Cuenta> findByUsuarioAndActivoTrue(String usuario);
}
