package com.ulima.incidenciaurbana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "token_notificaciones")
public class TokenNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cuenta_id", nullable = false, unique = true)
    private Cuenta cuenta;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    public TokenNotificacion() {
    }

    public TokenNotificacion(Cuenta cuenta, String token) {
        this.cuenta = cuenta;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
