package com.example.ambulance.dto;

import com.example.ambulance.entity.EmergencyCall;

public class AssignmentResultDto {
    private Long emergencyCallId;
    private String callerName;
    private String locationZone;
    private String assignedAmbulanceVehicleNumber;
    private Long assignedAmbulanceId;
    private String ambulanceZone;
    private double distanceKm;
    private String status;
    private String message;

    public AssignmentResultDto() {}

    public AssignmentResultDto(EmergencyCall call, double distanceKm) {
        this.emergencyCallId = call.getId();
        this.callerName = call.getCallerName();
        this.locationZone = call.getLocationZone() != null ? call.getLocationZone().getName() : null;
        if (call.getAssignedAmbulance() != null) {
            this.assignedAmbulanceId = call.getAssignedAmbulance().getId();
            this.assignedAmbulanceVehicleNumber = call.getAssignedAmbulance().getVehicleNumber();
            if (call.getAssignedAmbulance().getCurrentZone() != null) {
                this.ambulanceZone = call.getAssignedAmbulance().getCurrentZone().getName();
            }
        }
        this.distanceKm = Math.round(distanceKm * 10.0) / 10.0;
        this.status = call.getStatus() != null ? call.getStatus().name() : null;
        this.message = "Emergency Call: #" + call.getId() + 
            " | Assigned Ambulance: " + (assignedAmbulanceVehicleNumber != null ? assignedAmbulanceVehicleNumber : "N/A") + 
            " | Distance: " + this.distanceKm + " km | Status: " + this.status;
    }

    public Long getEmergencyCallId() {
        return emergencyCallId;
    }

    public void setEmergencyCallId(Long emergencyCallId) {
        this.emergencyCallId = emergencyCallId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getLocationZone() {
        return locationZone;
    }

    public void setLocationZone(String locationZone) {
        this.locationZone = locationZone;
    }

    public String getAssignedAmbulanceVehicleNumber() {
        return assignedAmbulanceVehicleNumber;
    }

    public void setAssignedAmbulanceVehicleNumber(String assignedAmbulanceVehicleNumber) {
        this.assignedAmbulanceVehicleNumber = assignedAmbulanceVehicleNumber;
    }

    public Long getAssignedAmbulanceId() {
        return assignedAmbulanceId;
    }

    public void setAssignedAmbulanceId(Long assignedAmbulanceId) {
        this.assignedAmbulanceId = assignedAmbulanceId;
    }

    public String getAmbulanceZone() {
        return ambulanceZone;
    }

    public void setAmbulanceZone(String ambulanceZone) {
        this.ambulanceZone = ambulanceZone;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
