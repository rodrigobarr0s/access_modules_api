package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.HistoryAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitation_history")
public class SolicitationHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitation_id", nullable = false)
    private AccessSolicitation solicitation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryAction action; // CRIADO, CANCELADO, RENOVADO, NEGADO

    @Column
    private String reason; // motivo se houver

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    public SolicitationHistory() {
    }

    public SolicitationHistory(Long id, AccessSolicitation solicitation, HistoryAction action, String reason,
            LocalDateTime date) {
        this.id = id;
        this.solicitation = solicitation;
        this.action = action;
        this.reason = reason;
        this.date = date;
    }

    public SolicitationHistory(AccessSolicitation solicitation, HistoryAction action, String reason) {
        this.solicitation = solicitation;
        this.action = action;
        this.reason = reason;
        this.date = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccessSolicitation getSolicitation() {
        return solicitation;
    }

    public void setSolicitation(AccessSolicitation solicitation) {
        this.solicitation = solicitation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public HistoryAction getAction() {
        return action;
    }

    public void setAction(HistoryAction action) {
        this.action = action;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SolicitationHistory other = (SolicitationHistory) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
