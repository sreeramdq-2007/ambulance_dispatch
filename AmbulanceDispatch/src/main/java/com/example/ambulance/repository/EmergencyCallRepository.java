package com.example.ambulance.repository;

import com.example.ambulance.entity.EmergencyCall;
import com.example.ambulance.entity.CallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyCallRepository extends JpaRepository<EmergencyCall, Long> {
    List<EmergencyCall> findByStatus(CallStatus status);
}
