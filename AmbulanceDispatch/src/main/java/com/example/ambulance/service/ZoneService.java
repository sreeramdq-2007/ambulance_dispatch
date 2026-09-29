package com.example.ambulance.service;

import com.example.ambulance.entity.Zone;
import com.example.ambulance.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }
}
