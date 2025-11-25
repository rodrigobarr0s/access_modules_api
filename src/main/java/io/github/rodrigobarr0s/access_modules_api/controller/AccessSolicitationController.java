package io.github.rodrigobarr0s.access_modules_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<AccessSolicitationResponse>> create(
            @Valid @RequestBody AccessSolicitationRequest request) {
        List<AccessSolicitation> solicitations = service.create(request);
        List<AccessSolicitationResponse> responses = solicitations.stream()
                .map(AccessSolicitationResponse::new)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "Listar solicitações com filtros", security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping
    public ResponseEntity<Page<AccessSolicitationResponse>> findAll(
            @RequestParam(required = false) SolicitationStatus status,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Boolean urgente,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AccessSolicitation> solicitations = service.findWithFilters(
                status, moduleId, urgente, texto, startDate, endDate, pageable);

        Page<AccessSolicitationResponse> responses = solicitations.map(AccessSolicitationResponse::new);
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
