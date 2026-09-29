package com.example.ambulance.dto;

import com.example.ambulance.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmergencyCallRequestDto {

    @NotBlank(message = "Caller name is required")
    private String callerName;

    @NotNull(message = "Location Zone ID is required")
    private Long locationZoneId;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private boolean autoAssign = true;

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public Long getLocationZoneId() {
        return locationZoneId;
    }

    public void setLocationZoneId(Long locationZoneId) {
        this.locationZoneId = locationZoneId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isAutoAssign() {
        return autoAssign;
    }

    public void setAutoAssign(boolean autoAssign) {
        this.autoAssign = autoAssign;
    }
}
