package io.github.rodrigobarr0s.access_modules_api.dto;

import java.util.List;
import java.util.stream.Collectors;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;

public record ModuleAvailableResponse(
    Long id,
    String name,
    String description,
    List<String> allowedDepartments,
    boolean active,
    List<String> incompatibleModules
) {
    public ModuleAvailableResponse(Module module) {
        this(
            module.getId(),
            module.getName(),
            module.getDescription(),
            module.getAllowedDepartments().stream().collect(Collectors.toList()),
            module.isActive(),
            module.getIncompatibilities().stream()
                  .map(ModuleIncompatibility::getIncompatibleModule) // pega o módulo incompatível
                  .map(Module::getName)                              // pega o nome do módulo
                  .collect(Collectors.toList())
        );
    }
}
