package io.github.rodrigobarr0s.access_modules_api.controller;

import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitations")
public class AccessSolicitationController {

    private final AccessSolicitationService service;

    public AccessSolicitationController(AccessSolicitationService service) {
        this.service = service;
    }

    // Criar solicitação
    @PostMapping
    public ResponseEntity<AccessSolicitation> create(@RequestBody AccessSolicitation solicitation) {
        AccessSolicitation created = service.create(solicitation);
        return ResponseEntity.ok(created);
    }

    // Consultar solicitações com filtros
    @GetMapping
    public ResponseEntity<List<AccessSolicitation>> findAll(
            @RequestParam(required = false) SolicitationStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Boolean urgente) {
        List<AccessSolicitation> result = service.findWithFilters(status, userId, moduleId, urgente);
        return ResponseEntity.ok(result);
    }

    // Detalhar solicitação por protocolo
    @GetMapping("/{protocolo}")
    public ResponseEntity<AccessSolicitation> findByProtocolo(@PathVariable String protocolo) {
        AccessSolicitation solicitation = service.findByProtocolo(protocolo);
        return ResponseEntity.ok(solicitation);
    }

    // Cancelar solicitação
    @PostMapping("/{protocolo}/cancel")
    public ResponseEntity<AccessSolicitation> cancel(
            @PathVariable String protocolo,
            @RequestParam String reason) {
        AccessSolicitation canceled = service.cancel(protocolo, reason);
        return ResponseEntity.ok(canceled);
    }

    // Renovar solicitação
    @PostMapping("/{protocolo}/renew")
    public ResponseEntity<AccessSolicitation> renew(@PathVariable String protocolo) {
        AccessSolicitation renewed = service.renew(protocolo);
        return ResponseEntity.ok(renewed);
    }
}
