package io.github.rodrigobarr0s.access_modules_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.rodrigobarr0s.access_modules_api.dto.ModuleAvailableResponse;
import io.github.rodrigobarr0s.access_modules_api.service.ModuleService;

@RestController
@RequestMapping("/modules")
public class ModuleController {

    private final ModuleService service;

    public ModuleController(ModuleService service) {
        this.service = service;
    }

    @GetMapping("/available")
    public ResponseEntity<List<ModuleAvailableResponse>> listAvailableModules() {
        return ResponseEntity.ok(service.listAvailableModules());
    }
}
