package com.tn.homeless.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * NEW — was completely missing from the original project.
 * When a Shelter or HomelessPerson ID didn't exist, the original code
 * threw a generic NullPointerException with no useful HTTP status.
 * This produces a clean 404 response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
