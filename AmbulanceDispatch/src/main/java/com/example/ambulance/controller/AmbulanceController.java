package com.example.ambulance.controller;

import com.example.ambulance.dto.AmbulanceRequestDto;
import com.example.ambulance.dto.AmbulanceStatusUpdateDto;
import com.example.ambulance.entity.Ambulance;
import com.example.ambulance.service.AmbulanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambulances")
@CrossOrigin(origins = "*")
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    public AmbulanceController(AmbulanceService ambulanceService) {
        this.ambulanceService = ambulanceService;
    }

    @PostMapping
    public ResponseEntity<Ambulance> registerAmbulance(@Valid @RequestBody AmbulanceRequestDto requestDto) {
        Ambulance created = ambulanceService.registerAmbulance(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ambulance>> getAllAmbulances() {
        return ResponseEntity.ok(ambulanceService.getAllAmbulances());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Ambulance>> getAvailableAmbulances() {
        return ResponseEntity.ok(ambulanceService.getAvailableAmbulances());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ambulance> updateAmbulanceStatus(
            @PathVariable Long id,
            @Valid @RequestBody AmbulanceStatusUpdateDto dto) {
        Ambulance updated = ambulanceService.updateAmbulanceStatus(id, dto.getStatus());
        return ResponseEntity.ok(updated);
    }
}
