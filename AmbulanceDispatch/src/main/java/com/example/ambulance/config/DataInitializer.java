package com.example.ambulance.config;

import com.example.ambulance.entity.*;
import com.example.ambulance.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ZoneRepository zoneRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final EmergencyCallRepository emergencyCallRepository;

    public DataInitializer(ZoneRepository zoneRepository,
                           AmbulanceRepository ambulanceRepository,
                           EmergencyCallRepository emergencyCallRepository) {
        this.zoneRepository = zoneRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.emergencyCallRepository = emergencyCallRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (zoneRepository.count() == 0) {
            Zone z1 = zoneRepository.save(new Zone("Zone A - Downtown", "Central City (0, 0)", 0.0, 0.0));
            Zone z2 = zoneRepository.save(new Zone("Zone B - North Hill", "North District (3, 4)", 3.0, 4.0));
            Zone z3 = zoneRepository.save(new Zone("Zone C - East Bay", "East District (6, 8)", 6.0, 8.0));
            Zone z4 = zoneRepository.save(new Zone("Zone D - South Park", "South District (1, -5)", 1.0, -5.0));
            Zone z5 = zoneRepository.save(new Zone("Zone E - West End", "West District (-4, 2)", -4.0, 2.0));

            if (ambulanceRepository.count() == 0) {
                ambulanceRepository.save(new Ambulance("TN-01-1001", AmbulanceStatus.AVAILABLE, z1));
                ambulanceRepository.save(new Ambulance("TN-01-1002", AmbulanceStatus.AVAILABLE, z2));
                ambulanceRepository.save(new Ambulance("TN-01-1003", AmbulanceStatus.BUSY, z3));
                ambulanceRepository.save(new Ambulance("TN-01-1004", AmbulanceStatus.MAINTENANCE, z4));
                ambulanceRepository.save(new Ambulance("TN-01-1005", AmbulanceStatus.OUT_OF_SERVICE, z5));
            }

            if (emergencyCallRepository.count() == 0) {
                EmergencyCall call1 = new EmergencyCall("John Doe", z2, Priority.HIGH);
                call1.setStatus(CallStatus.PENDING);
                emergencyCallRepository.save(call1);

                EmergencyCall call2 = new EmergencyCall("Sarah Smith", z3, Priority.MEDIUM);
                Ambulance busyAmb = ambulanceRepository.findByVehicleNumber("TN-01-1003").orElse(null);
                call2.setAssignedAmbulance(busyAmb);
                call2.setStatus(CallStatus.ASSIGNED);
                call2.setCalculatedDistanceKm(5.0);
                emergencyCallRepository.save(call2);
            }
        }
    }
}
