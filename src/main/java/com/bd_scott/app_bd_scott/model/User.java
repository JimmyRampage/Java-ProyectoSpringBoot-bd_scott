package com.bd_scott.app_bd_scott.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roles")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(unique = true, nullable = false, length = 45)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    // Relación Muchos a Muchos: Un usuario tiene muchos roles, un rol lo tienen muchos usuarios
    // EAGER: Cuando cargo el usuario, quiero sus roles INMEDIATAMENTE para seguridad
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles", // Nombre de la tabla intermedia automática
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles = new HashSet<>();

    // Helper para agregar roles fácilmente
    public void addRole(Role role) {
        this.roles.add(role);
    }
}
