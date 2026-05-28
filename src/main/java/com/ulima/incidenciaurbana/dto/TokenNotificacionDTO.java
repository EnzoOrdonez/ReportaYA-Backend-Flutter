package com.ulima.incidenciaurbana.dto;

public class TokenNotificacionDTO {
    private Long cuentaId;
    private String token;

    public TokenNotificacionDTO() {
    }

    public TokenNotificacionDTO(Long cuentaId, String token) {
        this.cuentaId = cuentaId;
        this.token = token;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
