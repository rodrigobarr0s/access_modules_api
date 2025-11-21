package io.github.rodrigobarr0s.access_modules_api.service.exception;

import java.io.Serial;

public class DuplicateEntityException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public DuplicateEntityException(String entityName, String identifier) {
        super(entityName + " já existe: " + identifier);
    }
}
