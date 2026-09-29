package com.example.ambulance.service;

import com.example.ambulance.dto.AssignmentResultDto;
import com.example.ambulance.dto.EmergencyCallRequestDto;
import com.example.ambulance.entity.*;
import com.example.ambulance.exception.BusyAmbulanceException;
import com.example.ambulance.exception.InvalidRequestException;
import com.example.ambulance.exception.NoAvailableAmbulanceException;
import com.example.ambulance.exception.ResourceNotFoundException;
import com.example.ambulance.repository.AmbulanceRepository;
import com.example.ambulance.repository.EmergencyCallRepository;
import com.example.ambulance.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmergencyCallService {

    private final EmergencyCallRepository emergencyCallRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final ZoneRepository zoneRepository;

    public EmergencyCallService(EmergencyCallRepository emergencyCallRepository,
                                AmbulanceRepository ambulanceRepository,
                                ZoneRepository zoneRepository) {
        this.emergencyCallRepository = emergencyCallRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<EmergencyCall> getAllEmergencyCalls() {
        return emergencyCallRepository.findAll();
    }

    public EmergencyCall getEmergencyCallById(Long id) {
        return emergencyCallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency Call not found with ID: " + id));
    }

    @Transactional
    public EmergencyCall createEmergencyCall(EmergencyCallRequestDto dto) {
        Zone zone = zoneRepository.findById(dto.getLocationZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + dto.getLocationZoneId()));

        EmergencyCall call = new EmergencyCall(
                dto.getCallerName().trim(),
                zone,
                dto.getPriority()
        );

        call = emergencyCallRepository.save(call);

        if (dto.isAutoAssign()) {
            try {
                assignNearestAmbulance(call.getId());
                call = getEmergencyCallById(call.getId());
            } catch (NoAvailableAmbulanceException e) {
                // Call remains PENDING when no ambulance is available
            }
        }

        return call;
    }

    @Transactional
    public AssignmentResultDto assignNearestAmbulance(Long emergencyCallId) {
        EmergencyCall call = getEmergencyCallById(emergencyCallId);

        if (call.getStatus() == CallStatus.COMPLETED || call.getStatus() == CallStatus.CANCELLED) {
            throw new InvalidRequestException("Cannot assign ambulance to a call with status: " + call.getStatus());
        }

        if (call.getStatus() == CallStatus.ASSIGNED && call.getAssignedAmbulance() != null) {
            Ambulance current = call.getAssignedAmbulance();
            if (current.getStatus() == AmbulanceStatus.BUSY) {
                double distance = calculateDistance(current.getCurrentZone(), call.getLocationZone());
                return new AssignmentResultDto(call, distance);
            }
        }

        // 1. Find AVAILABLE ambulances only
        List<Ambulance> availableAmbulances = ambulanceRepository.findByStatus(AmbulanceStatus.AVAILABLE);

        if (availableAmbulances.isEmpty()) {
            throw new NoAvailableAmbulanceException("No AVAILABLE ambulance found in system. All ambulances are currently BUSY, MAINTENANCE, or OUT_OF_SERVICE.");
        }

        // 2. Compare ambulance zone with call zone & find nearest
        Zone callZone = call.getLocationZone();
        Ambulance nearestAmbulance = null;
        double minDistance = Double.MAX_VALUE;

        for (Ambulance ambulance : availableAmbulances) {
            double distance = calculateDistance(ambulance.getCurrentZone(), callZone);
            if (distance < minDistance) {
                minDistance = distance;
                nearestAmbulance = ambulance;
            }
        }

        if (nearestAmbulance == null) {
            throw new NoAvailableAmbulanceException("Unable to determine nearest available ambulance.");
        }

        // Ensure nearest ambulance is AVAILABLE (never assign BUSY, MAINTENANCE, OUT_OF_SERVICE)
        if (nearestAmbulance.getStatus() != AmbulanceStatus.AVAILABLE) {
            throw new BusyAmbulanceException("Selected ambulance " + nearestAmbulance.getVehicleNumber() + " is not available.");
        }

        // 3. Assign ambulance & update statuses
        nearestAmbulance.setStatus(AmbulanceStatus.BUSY);
        nearestAmbulance.setLastUpdated(LocalDateTime.now());
        ambulanceRepository.save(nearestAmbulance);

        call.setAssignedAmbulance(nearestAmbulance);
        call.setStatus(CallStatus.ASSIGNED);
        call.setCalculatedDistanceKm(Math.round(minDistance * 10.0) / 10.0);
        call = emergencyCallRepository.save(call);

        return new AssignmentResultDto(call, minDistance);
    }

    @Transactional
    public EmergencyCall updateCallStatus(Long id, CallStatus newStatus) {
        EmergencyCall call = getEmergencyCallById(id);

        // If completing or cancelling call, release the assigned ambulance back to AVAILABLE
        if ((newStatus == CallStatus.COMPLETED || newStatus == CallStatus.CANCELLED) 
                && call.getAssignedAmbulance() != null) {
            Ambulance ambulance = call.getAssignedAmbulance();
            if (ambulance.getStatus() == AmbulanceStatus.BUSY) {
                ambulance.setStatus(AmbulanceStatus.AVAILABLE);
                ambulance.setLastUpdated(LocalDateTime.now());
                ambulanceRepository.save(ambulance);
            }
        }

        call.setStatus(newStatus);
        return emergencyCallRepository.save(call);
    }

    public double calculateDistance(Zone z1, Zone z2) {
        if (z1 == null || z2 == null) {
            return 5.0;
        }
        if (z1.getId() != null && z1.getId().equals(z2.getId())) {
            return 2.0; // Same zone
        }

        double dx = z1.getCoordinateX() - z2.getCoordinateX();
        double dy = z1.getCoordinateY() - z2.getCoordinateY();
        double euclidean = Math.sqrt(dx * dx + dy * dy);

        return Math.max(1.5, Math.round(euclidean * 10.0) / 10.0);
    }
}
