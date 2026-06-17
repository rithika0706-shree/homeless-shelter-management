package com.tn.homeless.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * NEW — was completely missing from the original project.
 * The original AdmissionServiceImpl had no capacity check before admitting
 * a person, which could push currentOccupancy beyond totalCapacity.
 * This exception + check prevents that.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ShelterFullException extends RuntimeException {
    public ShelterFullException(String shelterName) {
        super("Shelter '" + shelterName + "' is at full capacity. No beds available.");
    }
}
