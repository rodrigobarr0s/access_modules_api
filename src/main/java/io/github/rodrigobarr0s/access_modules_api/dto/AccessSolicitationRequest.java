package io.github.rodrigobarr0s.access_modules_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class AccessSolicitationRequest {

    @NotNull(message = "Os módulos são obrigatórios")
    @Size(min = 1, max = 3, message = "É necessário selecionar entre 1 e 3 módulos")
    private List<Long> moduleIds;

    @NotBlank(message = "A justificativa não pode estar vazia")
    @Size(min = 20, max = 500, message = "A justificativa deve ter entre 20 e 500 caracteres")
    private String justificativa;

    private boolean urgente;

    public AccessSolicitationRequest() {
    }

    public AccessSolicitationRequest(
            @NotNull(message = "Os módulos são obrigatórios") @Size(min = 1, max = 3, message = "É necessário selecionar entre 1 e 3 módulos") List<Long> moduleIds,
            @NotBlank(message = "A justificativa não pode estar vazia") @Size(min = 20, max = 500, message = "A justificativa deve ter entre 20 e 500 caracteres") String justificativa,
            boolean urgente) {
        this.moduleIds = moduleIds;
        this.justificativa = justificativa;
        this.urgente = urgente;
    }

    public List<Long> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Long> moduleIds) {
        this.moduleIds = moduleIds;
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
