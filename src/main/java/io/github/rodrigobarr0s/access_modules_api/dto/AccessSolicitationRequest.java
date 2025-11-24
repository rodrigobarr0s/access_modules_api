package io.github.rodrigobarr0s.access_modules_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AccessSolicitationRequest {

    @NotNull(message = "O módulo é obrigatório")
    private Long moduleId;

    @NotBlank(message = "A justificativa não pode estar vazia")
    private String justificativa;

    private boolean urgente;

    public AccessSolicitationRequest() {
    }

    public AccessSolicitationRequest(
            @NotNull(message = "O módulo é obrigatório") Long moduleId,
            @NotBlank(message = "A justificativa não pode estar vazia") String justificativa,
            boolean urgente) {
        this.moduleId = moduleId;
        this.justificativa = justificativa;
        this.urgente = urgente;
    }

    // Getters e Setters
    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }
}
