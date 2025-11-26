package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "modules")
public class Module implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do módulo é obrigatório")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String description;

    // Departamentos permitidos
    @ElementCollection
    @CollectionTable(name = "module_departments", joinColumns = @JoinColumn(name = "module_id"))
    @Column(name = "department")
    private Set<String> allowedDepartments = new HashSet<>();

    // Indicador se o módulo está ativo
    @Column(nullable = false)
    private boolean active = true;

    // Relacionamento com acessos de usuários
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserModuleAccess> accesses = new HashSet<>();

    // Relacionamento com incompatibilidades
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ModuleIncompatibility> incompatibilities = new HashSet<>();

    // Construtores
    public Module() {
    }

    public Module(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getAllowedDepartments() {
        return allowedDepartments;
    }

    public void setAllowedDepartments(Set<String> allowedDepartments) {
        this.allowedDepartments = allowedDepartments;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<UserModuleAccess> getAccesses() {
        return accesses;
    }

    public void setAccesses(Set<UserModuleAccess> accesses) {
        this.accesses = accesses;
    }

    public Set<ModuleIncompatibility> getIncompatibilities() {
        return incompatibilities;
    }

    public void setIncompatibilities(Set<ModuleIncompatibility> incompatibilities) {
        this.incompatibilities = incompatibilities;
    }

    // Métodos auxiliares
    public void addAccess(UserModuleAccess access) {
        this.accesses.add(access);
        access.setModule(this);
    }

    public void removeAccess(UserModuleAccess access) {
        this.accesses.remove(access);
        access.setModule(null);
    }

    public void addIncompatibility(ModuleIncompatibility incompatibility) {
        this.incompatibilities.add(incompatibility);
        incompatibility.setModule(this);
    }

    public void removeIncompatibility(ModuleIncompatibility incompatibility) {
        this.incompatibilities.remove(incompatibility);
        incompatibility.setModule(null);
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Module))
            return false;
        Module other = (Module) o;
        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Module{id=" + id + ", name='" + name + "'}";
    }
}
