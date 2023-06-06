package com.navutil.server.data.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Objects;

@Entity
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Email
    @NotBlank
    @Column(name = "email", nullable = false, length = 64)
    private String email;
    @Basic
    @NotBlank
    @Column(name = "name", length = 64)
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Basic
    @NotBlank
    @Column(name = "password", nullable = false, length = 128)
    private String password;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role roleByRoleId;
    @OneToMany(mappedBy = "userByUserId", orphanRemoval = true)
    private Collection<Marker> markersById;
    @OneToMany(mappedBy = "userByUserId", orphanRemoval = true)
    private Collection<Route> routesById;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Role getRoleByRoleId() {
        return roleByRoleId;
    }

    public void setRoleByRoleId(Role roleByRoleId) {
        this.roleByRoleId = roleByRoleId;
    }

    public Collection<Marker> getMarkersById() {
        return markersById;
    }

    public Collection<Route> getRoutesById() {
        return routesById;
    }

    public User() {
    }

    public User(String email, String name, String password, Role roleByRoleId) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.roleByRoleId = roleByRoleId;
    }
}
