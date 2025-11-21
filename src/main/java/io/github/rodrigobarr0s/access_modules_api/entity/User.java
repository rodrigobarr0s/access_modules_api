package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Armazena o código do enum no banco
    @Column(nullable = false)
    private Integer role;

    // Muitos-para-muitos com Module via UserModuleAccess
    @ManyToMany
    @JoinTable(name = "user_module_access", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "module_id"))
    private Set<Module> modules = new HashSet<>();

    // Construtores
    public User() {
    }

    public User(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        setRole(role);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter/Setter para trabalhar com Role como enum
    public Role getRole() {
        return role != null ? Role.valueOf(role) : null;
    }

    public void setRole(Role role) {
        if (role != null) {
            this.role = role.getCode();

        }
    }

    public Set<Module> getModules() {
        return modules;
    }

    // Métodos auxiliares para gerenciar relação com Module
    public void addModule(Module module) {
        this.modules.add(module);
        module.getUsers().add(this);
    }

    public void removeModule(Module module) {
        this.modules.remove(module);
        module.getUsers().remove(this);
    }

    // equals e hashCode baseados em id
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString para debug/log
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + getRole() + "'}";
    }

}
