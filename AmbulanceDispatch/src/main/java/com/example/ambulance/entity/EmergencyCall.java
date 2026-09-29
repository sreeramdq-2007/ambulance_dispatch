package com.example.ambulance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_calls")
public class EmergencyCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Caller name is required")
    @Column(name = "caller_name", nullable = false)
    private String callerName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_zone_id", nullable = false)
    private Zone locationZone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_ambulance_id")
    private Ambulance assignedAmbulance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "calculated_distance_km")
    private Double calculatedDistanceKm;

    public EmergencyCall() {}

    public EmergencyCall(String callerName, Zone locationZone, Priority priority) {
        this.callerName = callerName;
        this.locationZone = locationZone;
        this.priority = priority;
        this.status = CallStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = CallStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public Zone getLocationZone() {
        return locationZone;
    }

    public void setLocationZone(Zone locationZone) {
        this.locationZone = locationZone;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public Ambulance getAssignedAmbulance() {
        return assignedAmbulance;
    }

    public void setAssignedAmbulance(Ambulance assignedAmbulance) {
        this.assignedAmbulance = assignedAmbulance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getCalculatedDistanceKm() {
        return calculatedDistanceKm;
    }

    public void setCalculatedDistanceKm(Double calculatedDistanceKm) {
        this.calculatedDistanceKm = calculatedDistanceKm;
    }
}
