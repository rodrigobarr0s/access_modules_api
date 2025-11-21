package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "module_incompatibility", uniqueConstraints = @UniqueConstraint(columnNames = { "module_id",
        "incompatible_module_id" }))
public class ModuleIncompatibility implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Módulo principal
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    // Módulo incompatível
    @ManyToOne
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
