package io.github.rodrigobarr0s.access_modules_api.entity.enums;

public enum SolicitationStatus {
    ATIVO(1),
    NEGADO(2),
    CANCELADO(3);

    private final int code;

    SolicitationStatus(int code) {
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
