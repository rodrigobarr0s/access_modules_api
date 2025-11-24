package io.github.rodrigobarr0s.access_modules_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationRequest;
import io.github.rodrigobarr0s.access_modules_api.dto.AccessSolicitationResponse;
import io.github.rodrigobarr0s.access_modules_api.dto.CancelRequest;
import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/solicitations")
public class AccessSolicitationController {

    private final AccessSolicitationService service;

    public AccessSolicitationController(AccessSolicitationService service) {
        this.service = service;
    }

    @Operation(summary = "Criar solicitação", security = { @SecurityRequirement(name = "bearerAuth") })
    @PostMapping
    public ResponseEntity<AccessSolicitationResponse> create(@Valid @RequestBody AccessSolicitationRequest request) {
        AccessSolicitation solicitation = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccessSolicitationResponse(solicitation));
    }

    @Operation(summary = "Listar solicitações com filtros", security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping
    public ResponseEntity<List<AccessSolicitationResponse>> findAll(
            @RequestParam(required = false) SolicitationStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Boolean urgente) {
        List<AccessSolicitation> solicitations = service.findWithFilters(status, userId, moduleId, urgente);
        List<AccessSolicitationResponse> responses = solicitations.stream()
                .map(AccessSolicitationResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar solicitação por protocolo", security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping("/{protocolo}")
    public ResponseEntity<AccessSolicitationResponse> findByProtocolo(@PathVariable String protocolo) {
        AccessSolicitation solicitation = service.findByProtocolo(protocolo);
        return ResponseEntity.ok(new AccessSolicitationResponse(solicitation));
    }

    @Operation(summary = "Cancelar solicitação", security = { @SecurityRequirement(name = "bearerAuth") })
    @PatchMapping("/{protocolo}/cancel")
    public ResponseEntity<AccessSolicitationResponse> cancel(
            @PathVariable String protocolo,
            @Valid @RequestBody CancelRequest cancelRequest) {
        AccessSolicitation solicitation = service.cancel(protocolo, cancelRequest.getReason());
        return ResponseEntity.ok(new AccessSolicitationResponse(solicitation));
    }

    @Operation(summary = "Renovar solicitação", security = { @SecurityRequirement(name = "bearerAuth") })
    @PatchMapping("/{protocolo}/renew")
    public ResponseEntity<AccessSolicitationResponse> renew(@PathVariable String protocolo) {
        AccessSolicitation solicitation = service.renew(protocolo);
        return ResponseEntity.ok(new AccessSolicitationResponse(solicitation));
    }
}
