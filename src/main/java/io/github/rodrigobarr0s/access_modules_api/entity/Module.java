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

    // Relacionamento com UserModuleAccess
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

    public Set<UserModuleAccess> getAccesses() {
        return accesses;
    }

    public Set<ModuleIncompatibility> getIncompatibilities() {
        return incompatibilities;
    }

    // Métodos auxiliares para manter consistência nos relacionamentos
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Module))
            return false;
        Module other = (Module) o;

        // Se ambos têm id definido, compara por id
        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }

        // Se id é null, compara pelo nome
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
