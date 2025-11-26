package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.HistoryAction;
import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "access_solicitation")
public class AccessSolicitation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuário que solicita acesso
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Módulo solicitado
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false, unique = true, length = 50)
    private String protocolo;

    // Armazena o código do enum no banco
    @Column(nullable = false)
    private Integer status;

    @Column(length = 500)
    private String justificativa;

    @Column(nullable = false)
    private boolean urgente = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    @Column(length = 200)
    private String cancelReason;

    @Column(length = 500)
    private String negationReason;

    // Histórico de alterações
    @OneToMany(mappedBy = "solicitation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitationHistory> history = new ArrayList<>();

    // Solicitação anterior (para suportar renovação)
    @ManyToOne
    @JoinColumn(name = "previous_solicitation_id")
    private AccessSolicitation previousSolicitation;

    // Construtores
    public AccessSolicitation() {
    }

    public AccessSolicitation(Long id, User user, Module module, String protocolo,
            SolicitationStatus status, String justificativa,
            boolean urgente, LocalDateTime createdAt,
            LocalDateTime updatedAt, LocalDateTime expiresAt,
            String cancelReason, String negationReason) {
        this.id = id;
        this.user = user;
        this.module = module;
        this.protocolo = protocolo;
        setStatus(status);
        this.justificativa = justificativa;
        this.urgente = urgente;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.cancelReason = cancelReason;
        this.negationReason = negationReason;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos utilitários para histórico
    public void addHistory(HistoryAction action, String reason) {
        SolicitationHistory entry = new SolicitationHistory(this, action, reason);
        this.history.add(entry);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public SolicitationStatus getStatus() {
        return status != null ? SolicitationStatus.valueOf(status) : null;
    }

    public void setStatus(SolicitationStatus status) {
        if (status != null) {
            this.status = status.getCode();
        }
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getNegationReason() {
        return negationReason;
    }

    public void setNegationReason(String negationReason) {
        this.negationReason = negationReason;
    }

    public List<SolicitationHistory> getHistory() {
        return history;
    }

    public void setHistory(List<SolicitationHistory> history) {
        this.history = history;
    }

    public AccessSolicitation getPreviousSolicitation() {
        return previousSolicitation;
    }

    public void setPreviousSolicitation(AccessSolicitation previousSolicitation) {
        this.previousSolicitation = previousSolicitation;
    }

    // equals e hashCode baseados em id
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AccessSolicitation))
            return false;
        AccessSolicitation that = (AccessSolicitation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString para debug/log
    @Override
    public String toString() {
        return "AccessSolicitation{" +
                "id=" + id +
                ", protocolo='" + protocolo + '\'' +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", module=" + (module != null ? module.getName() : "null") +
                ", status=" + getStatus() +
                ", justificativa='" + justificativa + '\'' +
                ", urgente=" + urgente +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                ", cancelReason='" + cancelReason + '\'' +
                ", negationReason='" + negationReason + '\'' +
                '}';
    }
}
