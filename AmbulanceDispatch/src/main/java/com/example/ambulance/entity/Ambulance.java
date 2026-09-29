package com.example.ambulance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "ambulances")
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle number is required")
    @Column(name = "vehicle_number", nullable = false, unique = true)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmbulanceStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone currentZone;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Ambulance() {}

    public Ambulance(String vehicleNumber, AmbulanceStatus status, Zone currentZone) {
        this.vehicleNumber = vehicleNumber;
        this.status = status;
        this.currentZone = currentZone;
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public AmbulanceStatus getStatus() {
        return status;
    }

    public void setStatus(AmbulanceStatus status) {
        this.status = status;
    }

    public Zone getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(Zone currentZone) {
        this.currentZone = currentZone;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
