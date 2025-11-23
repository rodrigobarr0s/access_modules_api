package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "module_incompatibility", uniqueConstraints = @UniqueConstraint(columnNames = { "module_id",
        "incompatible_module_id" }))
public class ModuleIncompatibility implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O módulo principal é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @NotNull(message = "O módulo incompatível é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incompatible_module_id", nullable = false)
    private Module incompatibleModule;

    // Construtores
    public ModuleIncompatibility() {
    }

    public ModuleIncompatibility(Long id, Module module, Module incompatibleModule) {
        this.id = id;
        this.module = module;
        this.incompatibleModule = incompatibleModule;
    }

    public ModuleIncompatibility(Module module, Module incompatibleModule) {
        this.module = module;
        this.incompatibleModule = incompatibleModule;
    }

    @PrePersist
    @PreUpdate
    public void validate() {
        if (module != null && module.equals(incompatibleModule)) {
            throw new IllegalArgumentException("Um módulo não pode ser incompatível consigo mesmo.");
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Module getIncompatibleModule() {
        return incompatibleModule;
    }

    public void setIncompatibleModule(Module incompatibleModule) {
        this.incompatibleModule = incompatibleModule;
    }

    // equals e hashCode baseados em id
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ModuleIncompatibility))
            return false;
        ModuleIncompatibility that = (ModuleIncompatibility) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString para debug/log
    @Override
    public String toString() {
        return "ModuleIncompatibility{id=" + id +
                ", module=" + (module != null ? module.getName() : "null") +
                ", incompatibleModule=" + (incompatibleModule != null ? incompatibleModule.getName() : "null") +
                '}';
    }
}
