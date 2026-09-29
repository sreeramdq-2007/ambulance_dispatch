package com.example.ambulance.dto;

import com.example.ambulance.entity.AmbulanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AmbulanceRequestDto {

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotNull(message = "Zone ID is required")
    private Long zoneId;

    private AmbulanceStatus status;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public AmbulanceStatus getStatus() {
        return status;
    }

    public void setStatus(AmbulanceStatus status) {
        this.status = status;
    }
}
