package com.ulima.incidenciaurbana.controller;

import com.ulima.incidenciaurbana.dto.AsignacionDTO;
import com.ulima.incidenciaurbana.service.IAsignacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "*")
public class AsignacionController {

    private final IAsignacionService asignacionService;

    @Autowired
    public AsignacionController(IAsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @PostMapping
    public ResponseEntity<?> crearAsignacion(@Valid @RequestBody AsignacionDTO asignacionDTO) {
        try {
            AsignacionDTO asignacionCreada = asignacionService.crearAsignacion(asignacionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(asignacionCreada);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
