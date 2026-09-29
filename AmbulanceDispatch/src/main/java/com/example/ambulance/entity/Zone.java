package com.example.ambulance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Zone name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "coordinate_x")
    private double coordinateX;

    @Column(name = "coordinate_y")
    private double coordinateY;

    public Zone() {}

    public Zone(String name, String location, double coordinateX, double coordinateY) {
        this.name = name;
        this.location = location;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(id, zone.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
