package com.example.ambulance.controller;

import com.example.ambulance.dto.AssignmentResultDto;
import com.example.ambulance.dto.EmergencyCallRequestDto;
import com.example.ambulance.entity.CallStatus;
import com.example.ambulance.entity.EmergencyCall;
import com.example.ambulance.service.EmergencyCallService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency-calls")
@CrossOrigin(origins = "*")
public class EmergencyCallController {

    private final EmergencyCallService emergencyCallService;

    public EmergencyCallController(EmergencyCallService emergencyCallService) {
        this.emergencyCallService = emergencyCallService;
    }

    @PostMapping
    public ResponseEntity<EmergencyCall> createEmergencyCall(@Valid @RequestBody EmergencyCallRequestDto requestDto) {
        EmergencyCall call = emergencyCallService.createEmergencyCall(requestDto);
        return new ResponseEntity<>(call, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmergencyCall>> getAllEmergencyCalls() {
        return ResponseEntity.ok(emergencyCallService.getAllEmergencyCalls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyCall> getEmergencyCallById(@PathVariable Long id) {
        return ResponseEntity.ok(emergencyCallService.getEmergencyCallById(id));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<AssignmentResultDto> assignAmbulance(@PathVariable Long id) {
        AssignmentResultDto result = emergencyCallService.assignNearestAmbulance(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EmergencyCall> updateCallStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        CallStatus status = CallStatus.valueOf(statusStr.toUpperCase());
        EmergencyCall updated = emergencyCallService.updateCallStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
