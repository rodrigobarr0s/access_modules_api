package io.github.rodrigobarr0s.access_modules_api.entity.enums;

/**
 * Enum que representa os perfis de usuário do sistema.
 * Cada perfil é armazenado como código inteiro no banco.
 */
public enum Role {
    ADMIN(1),
    FINANCEIRO(2),
    RH(3),
    OPERACOES(4),
    TI(5),
    AUDITOR(6);

    private final int code;

    Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Role fromCode(int code) {
        for (Role value : Role.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de perfil de usuário inválido: " + code);
    }
}
