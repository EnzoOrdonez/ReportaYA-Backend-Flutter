package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.CuentaDTO;
import com.ulima.incidenciaurbana.service.ICuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller unificado para la creación de cuentas usando Abstract Factory
 * 
 * Ruta única: POST /api/cuenta
 * - Para CIUDADANO: No requiere autenticación (público)
 * - Para TECNICO: Requiere autenticación (protegido) - TODO: Implementar con Spring Security
 * - Para OPERADOR_MUNICIPAL: Requiere autenticación (protegido) - TODO: Implementar con Spring Security
 */
@RestController
@RequestMapping("/api/cuenta")
@CrossOrigin(origins = "*")
public class CuentaController {
    
    private final ICuentaService cuentaService;
    
    @Autowired
    public CuentaController(ICuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }
    
    /**
     * Endpoint unificado para crear cuentas de cualquier tipo
     * POST /api/cuenta
     * 
     * @param cuentaDTO Datos de la cuenta a crear (incluye tipoCuenta: CIUDADANO, TECNICO, OPERADOR_MUNICIPAL)
     * @return CuentaDTO con los datos de la cuenta creada
     * 
     * Ejemplos de uso:
     * 
     * 1. Crear Ciudadano (público - sin autenticación):
     * {
     *   "tipoCuenta": "CIUDADANO",
     *   "usuario": "jperez",
     *   "contrasena": "password123",
     *   "nombres": "Juan",
     *   "apellidos": "Pérez",
     *   "dni": "12345678",
     *   "telefono": "987654321",
     *   "correo": "juan@email.com",
     *   "activo": true
     * }
     * 
     * 2. Crear Técnico (protegido - requiere autenticación de administrador):
     * {
     *   "tipoCuenta": "TECNICO",
     *   "usuario": "mtecnico",
     *   ...
     * }
     * 
     * 3. Crear Operador Municipal (protegido - requiere autenticación de administrador):
     * {
     *   "tipoCuenta": "OPERADOR_MUNICIPAL",
     *   "usuario": "operador1",
     *   ...
     * }
     */
    @PostMapping
    public ResponseEntity<?> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        try {
            // TODO: Implementar validación de autenticación con Spring Security
            // Si tipoCuenta es TECNICO u OPERADOR_MUNICIPAL, verificar que el usuario esté autenticado
            // y tenga rol de ADMIN
            
            // Validación temporal: Por ahora permitimos crear cualquier tipo de cuenta
            // En producción, descomentar la validación de seguridad:
            /*
            if (!cuentaDTO.getTipoCuenta().equals("CIUDADANO")) {
                // Aquí se debería verificar el token JWT y el rol del usuario
                // throw new UnauthorizedException("No autorizado para crear este tipo de cuenta");
            }
            */
            
            CuentaDTO cuentaCreada = cuentaService.crearCuenta(cuentaDTO);
            return new ResponseEntity<>(cuentaCreada, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            // Error de tipo de cuenta inválido
            Map<String, String> error = new HashMap<>();
            error.put("error", "Tipo de cuenta inválido");
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            
        } catch (RuntimeException e) {
            // Error de duplicación o validación (DNI, correo, usuario duplicados)
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear la cuenta");
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
