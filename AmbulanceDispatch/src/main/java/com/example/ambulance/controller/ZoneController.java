package com.example.ambulance.controller;

import com.example.ambulance.entity.Zone;
import com.example.ambulance.service.ZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "*")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public ResponseEntity<List<Zone>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @PostMapping
    public ResponseEntity<Zone> createZone(@RequestBody Zone zone) {
        return new ResponseEntity<>(zoneService.createZone(zone), HttpStatus.CREATED);
    }
}
