package io.github.rodrigobarr0s.access_modules_api.entity.enums;

public enum Role {
    ADMIN(1),
    FINANCEIRO(2),
    RH(3),
    OPERACOES(4),
    TI(5),
    AUDITOR(6);

    private final int code;

    private Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Role valueOf(int code) {
        for (Role value : Role.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de perfil de usuário inválido: " + code);
    }
}
