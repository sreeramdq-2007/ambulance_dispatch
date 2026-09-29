package com.example.ambulance.exception;

public class NoAvailableAmbulanceException extends RuntimeException {
    public NoAvailableAmbulanceException(String message) {
        super(message);
    }
}
