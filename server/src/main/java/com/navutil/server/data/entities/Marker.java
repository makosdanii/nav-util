package com.navutil.server.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navutil.server.data.validation.NonValidatedOnPersistTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Objects;

@Entity
public class Marker {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @NotBlank
    @Column(name = "name", nullable = false, length = 64)
    private String name;
    @Basic
    @Min(1)
    @Max(25)
    @Column(name = "idx", nullable = false)
    private int idx;
    @NotNull
    @Basic
    @Column(name = "lat", nullable = false, precision = 0)
    private double lat;
    @NotNull
    @Basic
    @Column(name = "lng", nullable = false, precision = 0)
    private double lng;
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

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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
        Marker marker = (Marker) o;
        return id == marker.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
