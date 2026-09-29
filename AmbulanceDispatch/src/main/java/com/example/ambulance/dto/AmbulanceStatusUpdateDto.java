package com.example.ambulance.dto;

import com.example.ambulance.entity.AmbulanceStatus;
import jakarta.validation.constraints.NotNull;

public class AmbulanceStatusUpdateDto {

    @NotNull(message = "Status is required")
    private AmbulanceStatus status;

    public AmbulanceStatus getStatus() {
        return status;
    }

    public void setStatus(AmbulanceStatus status) {
        this.status = status;
    }
}
