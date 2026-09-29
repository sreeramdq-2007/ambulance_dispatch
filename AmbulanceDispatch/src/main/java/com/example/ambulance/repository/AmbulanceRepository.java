package com.example.ambulance.repository;

import com.example.ambulance.entity.Ambulance;
import com.example.ambulance.entity.AmbulanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByStatus(AmbulanceStatus status);
    Optional<Ambulance> findByVehicleNumber(String vehicleNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
}
