package io.github.rodrigobarr0s.access_modules_api.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelRequest {

    @NotBlank(message = "O motivo do cancelamento é obrigatório")
    private String reason;

    public CancelRequest() {
    }

    public CancelRequest(@NotBlank(message = "O motivo do cancelamento é obrigatório") String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
