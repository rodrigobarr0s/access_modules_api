package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

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

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

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

    @Override
    public String toString() {
        return "AccessSolicitation{id=" + id + ", user=" + user.getUsername() +
                ", module=" + module.getName() + ", status=" + status + "}";
    }
}
