package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "modules")
public class Module implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    // Muitos-para-muitos com User via UserModuleAccess
    @ManyToMany(mappedBy = "modules")
    private Set<User> users = new HashSet<>();

    // Relacionamento com incompatibilidades
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ModuleIncompatibility> incompatibilities = new HashSet<>();

    // Construtores
    public Module() {
    }

    public Module(Long id, String name, String description) {
        this.id = id;
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

    public Set<User> getUsers() {
        return users;
    }

    public Set<ModuleIncompatibility> getIncompatibilities() {
        return incompatibilities;
    }

    // Métodos auxiliares para manter consistência nos relacionamentos
    public void addUser(User user) {
        this.users.add(user);
        user.getModules().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getModules().remove(this);
    }

    public void addIncompatibility(ModuleIncompatibility incompatibility) {
        this.incompatibilities.add(incompatibility);
        incompatibility.setModule(this);
    }

    public void removeIncompatibility(ModuleIncompatibility incompatibility) {
        this.incompatibilities.remove(incompatibility);
        incompatibility.setModule(null);
    }

    // equals e hashCode baseados em id
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Module))
            return false;
        Module module = (Module) o;
        return Objects.equals(id, module.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString para debug/log
    @Override
    public String toString() {
        return "Module{id=" + id + ", name='" + name + "'}";
    }
}
