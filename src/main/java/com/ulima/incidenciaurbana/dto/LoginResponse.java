package com.ulima.incidenciaurbana.dto;

public class LoginResponse {
    private Long cuentaId;
    private String usuario;
    private String nombreCompleto;
    private String message;
    private String tipoCuenta;
    private String token;

    public LoginResponse(Long cuentaId, String usuario, String nombreCompleto, String message, String tipoCuenta) {
        this(cuentaId, usuario, nombreCompleto, message, tipoCuenta, null);
    }

    public LoginResponse(Long cuentaId, String usuario, String nombreCompleto, String message, String tipoCuenta, String token) {
        this.cuentaId = cuentaId;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.message = message;
        this.tipoCuenta = tipoCuenta;
        this.token = token;
    }

    public Long getCuentaId() { return cuentaId; }
    public String getUsuario() { return usuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getMessage() { return message; }
    public String getTipoCuenta() { return tipoCuenta; }
    public String getToken() { return token; }
}
