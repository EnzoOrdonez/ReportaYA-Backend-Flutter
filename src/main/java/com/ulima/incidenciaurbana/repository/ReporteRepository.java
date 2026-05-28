package com.ulima.incidenciaurbana.repository;

import com.ulima.incidenciaurbana.model.EstadoReporte;
import com.ulima.incidenciaurbana.model.Reporte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long>, JpaSpecificationExecutor<Reporte> {

        @EntityGraph(attributePaths = { "ubicacion", "cuenta", "cuenta.persona" })
        Page<Reporte> findByCuentaId(Long cuentaId, Pageable pageable);

        // Query paginada con EntityGraph para operaciones normales
        @EntityGraph(attributePaths = { "ubicacion", "cuenta", "cuenta.persona" })
        Page<Reporte> findByEstado(EstadoReporte estado, Pageable pageable);

        // Anthony-dev
        // Query con JOIN FETCH para cargar relaciones (sin paginación)
        // Usada cuando necesitamos todas las relaciones sin paginar
        @Query("SELECT DISTINCT r FROM Reporte r " +
                        "LEFT JOIN FETCH r.cuenta c " +
                        "LEFT JOIN FETCH c.persona " +
                        "LEFT JOIN FETCH r.ubicacion " +
                        "WHERE r.estado = :estado " +
                        "ORDER BY r.fechaCreacion DESC")
        List<Reporte> findByEstadoWithDetails(@Param("estado") EstadoReporte estado);

        // Query para obtener reporte por ID con todas las relaciones cargadas
        // Evita problema de "Duplicate row" al acceder a relaciones lazy
        @Query("SELECT DISTINCT r FROM Reporte r " +
                        "LEFT JOIN FETCH r.cuenta c " +
                        "LEFT JOIN FETCH c.persona " +
                        "LEFT JOIN FETCH r.ubicacion " +
                        "LEFT JOIN FETCH r.asignaciones a " +
                        "LEFT JOIN FETCH a.tecnico " +
                        "WHERE r.id = :id")
        Optional<Reporte> findByIdWithDetails(@Param("id") Long id);

        // Query paginada general - usa EntityGraph para todas las relaciones
        @Override
        @EntityGraph(attributePaths = { "ubicacion", "cuenta", "cuenta.persona" })
        Page<Reporte> findAll(Pageable pageable);
}
