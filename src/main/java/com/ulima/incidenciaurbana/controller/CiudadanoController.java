package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.CiudadanoDTO;
import com.ulima.incidenciaurbana.service.ICiudadanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para operaciones CRUD de Ciudadanos
 * NOTA: Para CREAR ciudadanos, usar el endpoint unificado: POST /api/cuenta
 * Este controller solo maneja: Leer, Actualizar, Eliminar
 */
@RestController
@RequestMapping("/api/ciudadanos")
@CrossOrigin(origins = "*")
public class CiudadanoController {
    
    private final ICiudadanoService ciudadanoService;
    
    @Autowired
    public CiudadanoController(ICiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }
    
    // NOTA: Método crearCiudadano eliminado
    // Usar POST /api/cuenta con tipoCuenta: "CIUDADANO"
    
    @GetMapping("/{id}")
    public ResponseEntity<CiudadanoDTO> obtenerCiudadano(@PathVariable Long id) {
        try {
            CiudadanoDTO ciudadano = ciudadanoService.obtenerCiudadanoPorId(id);
            return new ResponseEntity<>(ciudadano, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<CiudadanoDTO> actualizarCiudadano(
            @PathVariable Long id,
            @RequestBody CiudadanoDTO ciudadanoDTO) {
        try {
            CiudadanoDTO ciudadanoActualizado = ciudadanoService.actualizarCiudadano(id, ciudadanoDTO);
            return new ResponseEntity<>(ciudadanoActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCiudadano(@PathVariable Long id) {
        try {
            ciudadanoService.eliminarCiudadano(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
