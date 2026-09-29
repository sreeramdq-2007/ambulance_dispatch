package com.example.ambulance.service;

import com.example.ambulance.dto.AmbulanceRequestDto;
import com.example.ambulance.entity.Ambulance;
import com.example.ambulance.entity.AmbulanceStatus;
import com.example.ambulance.entity.Zone;
import com.example.ambulance.exception.InvalidRequestException;
import com.example.ambulance.exception.ResourceNotFoundException;
import com.example.ambulance.repository.AmbulanceRepository;
import com.example.ambulance.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final ZoneRepository zoneRepository;

    public AmbulanceService(AmbulanceRepository ambulanceRepository, ZoneRepository zoneRepository) {
        this.ambulanceRepository = ambulanceRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<Ambulance> getAllAmbulances() {
        return ambulanceRepository.findAll();
    }

    public List<Ambulance> getAvailableAmbulances() {
        return ambulanceRepository.findByStatus(AmbulanceStatus.AVAILABLE);
    }

    public Ambulance getAmbulanceById(Long id) {
        return ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambulance not found with ID: " + id));
    }

    @Transactional
    public Ambulance registerAmbulance(AmbulanceRequestDto requestDto) {
        if (ambulanceRepository.existsByVehicleNumber(requestDto.getVehicleNumber().trim().toUpperCase())) {
            throw new InvalidRequestException("Ambulance with vehicle number '" + requestDto.getVehicleNumber() + "' already exists");
        }

        Zone zone = zoneRepository.findById(requestDto.getZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + requestDto.getZoneId()));

        AmbulanceStatus initialStatus = requestDto.getStatus() != null ? requestDto.getStatus() : AmbulanceStatus.AVAILABLE;

        Ambulance ambulance = new Ambulance(
                requestDto.getVehicleNumber().trim().toUpperCase(),
                initialStatus,
                zone
        );

        return ambulanceRepository.save(ambulance);
    }

    @Transactional
    public Ambulance updateAmbulanceStatus(Long id, AmbulanceStatus newStatus) {
        Ambulance ambulance = getAmbulanceById(id);
        ambulance.setStatus(newStatus);
        ambulance.setLastUpdated(LocalDateTime.now());
        return ambulanceRepository.save(ambulance);
    }
}
