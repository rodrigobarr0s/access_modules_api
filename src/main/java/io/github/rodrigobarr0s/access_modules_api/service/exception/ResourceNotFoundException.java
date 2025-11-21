package io.github.rodrigobarr0s.access_modules_api.service.exception;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String entityName, String identifier) {
        super(entityName + " não encontrado: " + identifier);
    }
}
