package com.ulima.incidenciaurbana.dto;

public class CerrarReporteRequest {
    private Long reporteId;
    private Long operadorId;
    private String comentarioCierre;

    public CerrarReporteRequest() {
    }

    public CerrarReporteRequest(Long reporteId, Long operadorId, String comentarioCierre) {
        this.reporteId = reporteId;
        this.operadorId = operadorId;
        this.comentarioCierre = comentarioCierre;
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

    public String getComentarioCierre() {
        return comentarioCierre;
    }

    public void setComentarioCierre(String comentarioCierre) {
        this.comentarioCierre = comentarioCierre;
    }
}
