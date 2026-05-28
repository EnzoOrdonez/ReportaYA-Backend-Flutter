package com.ulima.incidenciaurbana.dto;

public class RechazarAuditoriaRequest {
    private Long reporteId;
    private Long operadorId;
    private String comentarioRechazo;

    public RechazarAuditoriaRequest() {
    }

    public RechazarAuditoriaRequest(Long reporteId, Long operadorId, String comentarioRechazo) {
        this.reporteId = reporteId;
        this.operadorId = operadorId;
        this.comentarioRechazo = comentarioRechazo;
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

    public String getComentarioRechazo() {
        return comentarioRechazo;
    }

    public void setComentarioRechazo(String comentarioRechazo) {
        this.comentarioRechazo = comentarioRechazo;
    }
}
