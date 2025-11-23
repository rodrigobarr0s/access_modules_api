package io.github.rodrigobarr0s.access_modules_api.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    // Armazena o código do enum no banco
    @Column(nullable = false)
    private Integer role;

    // Relacionamento com UserModuleAccess
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserModuleAccess> accesses = new HashSet<>();

    // Construtores
    public User() {
    }

    public User(Long id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        setRole(role);
    }

    public User(String email, String password, Role role) {
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role != null ? Role.fromCode(role) : null;
    }

    public void setRole(Role role) {
        if (role != null) {
            this.role = role.getCode();
        }
    }

    public Set<UserModuleAccess> getAccesses() {
        return accesses;
    }

    // Métodos auxiliares para gerenciar relação com UserModuleAccess
    public void addAccess(UserModuleAccess access) {
        this.accesses.add(access);
        access.setUser(this);
    }

    public void removeAccess(UserModuleAccess access) {
        this.accesses.remove(access);
        access.setUser(null);
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

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role='" + getRole() + "'}";
    }
}
