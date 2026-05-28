package com.ulima.incidenciaurbana.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Provides the BCryptPasswordEncoder bean for password hashing.
 * This is the minimum security infrastructure: only the encoder, no Spring Security filter chain.
 */
@Configuration
public class SecurityBeans {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
