package com.ulima.incidenciaurbana.dto;

public class RechazarDefinitivoRequest {
    private Long reporteId;
    private Long operadorId;
    private String motivo;

    public RechazarDefinitivoRequest() {
    }

    public RechazarDefinitivoRequest(Long reporteId, Long operadorId, String motivo) {
        this.reporteId = reporteId;
        this.operadorId = operadorId;
        this.motivo = motivo;
    }

    public Long getReporteId() {
        return reporteId;
    }

    public void setReporteId(Long reporteId) {
        this.reporteId = reporteId;
    }

    public Long getOperadorId() {
        return operadorId;
    }

    public void setOperadorId(Long operadorId) {
        this.operadorId = operadorId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
