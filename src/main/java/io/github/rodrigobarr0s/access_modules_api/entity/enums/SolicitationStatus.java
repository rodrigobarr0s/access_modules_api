package io.github.rodrigobarr0s.access_modules_api.entity.enums;

public enum SolicitationStatus {
    PENDING(1),
    APPROVED(2),
    REJECTED(3),
    CANCELED(4);

    private final int code;

    private SolicitationStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SolicitationStatus valueOf(int code) {
        for (SolicitationStatus value : SolicitationStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de status de solicitação inválido: " + code);
    }
}
