package com.navutil.server.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navutil.server.data.validation.NonValidatedOnPersistTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

import java.util.Objects;

@Entity
public class Route {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @NotBlank
    @Basic
    @Column(name = "name", nullable = false, length = 64)
    private String name;
    @NotBlank
    @Basic
    @Column(name = "route", nullable = false, length = -1)
    private String route;
    @JsonIgnore
    @ManyToOne
    @Null(groups = NonValidatedOnPersistTime.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userByUserId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public User getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(User userByUserId) {
        this.userByUserId = userByUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route1 = (Route) o;
        return id == route1.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
