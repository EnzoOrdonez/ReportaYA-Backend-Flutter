package com.ulima.incidenciaurbana.service.impl;

import com.ulima.incidenciaurbana.dto.LoginRequest;
import com.ulima.incidenciaurbana.dto.LoginResponse;
import com.ulima.incidenciaurbana.exception.InvalidCredentialsException;
import com.ulima.incidenciaurbana.model.Cuenta;
import com.ulima.incidenciaurbana.repository.CuentaRepository;
import com.ulima.incidenciaurbana.service.AuthService;
import com.ulima.incidenciaurbana.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CuentaRepository cuentaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(CuentaRepository cuentaRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.cuentaRepository = cuentaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Cuenta c = cuentaRepository.findByUsuarioAndActivoTrue(request.getUsuario())
                .orElseThrow(InvalidCredentialsException::new);

        String stored = c.getContrasenaHash();
        boolean valid;
        boolean needsMigration = false;

        if (stored != null && isBcryptHash(stored)) {
            valid = passwordEncoder.matches(request.getPassword(), stored);
        } else {
            valid = stored != null && stored.equals(request.getPassword());
            if (valid) {
                needsMigration = true;
            }
        }

        if (!valid) {
            throw new InvalidCredentialsException();
        }

        if (needsMigration) {
            c.setContrasenaHash(passwordEncoder.encode(request.getPassword()));
            cuentaRepository.save(c);
        }

        String nombre = (c.getPersona() != null) ? c.getPersona().getNombreCompleto() : "";
        String token = jwtUtil.generateToken(c.getId(), c.getUsuario(), c.getTipoCuenta());
        return new LoginResponse(c.getId(), c.getUsuario(), nombre, "Login exitoso", c.getTipoCuenta(), token);
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
