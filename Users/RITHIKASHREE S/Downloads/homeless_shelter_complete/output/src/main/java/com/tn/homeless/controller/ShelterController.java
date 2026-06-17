package com.tn.homeless.controller;

import com.tn.homeless.dto.ApiResponse;
import com.tn.homeless.entity.Shelter;
import com.tn.homeless.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * CHANGES from original:
 *  - Added @PreAuthorize role guards (original had none — any logged-in user
 *    could verify or delete shelters).
 *  - Added PUT /shelters/{id} update endpoint (was missing).
 *  - Added DELETE /shelters/{id} endpoint (was missing).
 *  - GET /shelters/available — new endpoint for volunteers to pick a shelter.
 *  - All responses wrapped in ApiResponse.
 */
@RestController
@RequestMapping("/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    /** GET /shelters — ADMIN or NGO can see all shelters */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<ApiResponse<List<Shelter>>> getAllShelters() {
        return ResponseEntity.ok(ApiResponse.ok("All shelters", shelterService.getAllShelters()));
    }

    /** GET /shelters/verified — visible to all authenticated users */
    @GetMapping("/verified")
    public ResponseEntity<ApiResponse<List<Shelter>>> getVerifiedShelters() {
        return ResponseEntity.ok(ApiResponse.ok("Verified shelters", shelterService.getVerifiedShelters()));
    }

    /** GET /shelters/available — volunteers see shelters with free beds */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Shelter>>> getAvailableShelters() {
        return ResponseEntity.ok(ApiResponse.ok("Available shelters", shelterService.getAvailableShelters()));
    }

    /** GET /shelters/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Shelter>> getShelterById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Shelter details", shelterService.getShelterById(id)));
    }

    /** POST /shelters — NGO registers a new shelter */
    @PostMapping
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Shelter>> addShelter(
            @RequestBody Shelter shelter, Authentication auth) {
        Shelter saved = shelterService.addShelter(shelter, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Shelter registered. Awaiting admin verification.", saved));
    }

    /** PUT /shelters/{id}/verify — ADMIN approves shelter */
    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Shelter>> verifyShelter(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Shelter verified", shelterService.verifyShelter(id)));
    }

    /** PUT /shelters/{id} — NGO or ADMIN updates shelter info */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Shelter>> updateShelter(
            @PathVariable Long id, @RequestBody Shelter shelter) {
        return ResponseEntity.ok(ApiResponse.ok("Shelter updated", shelterService.updateShelter(id, shelter)));
    }

    /** DELETE /shelters/{id} — ADMIN only */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShelter(@PathVariable Long id) {
        shelterService.deleteShelter(id);
        return ResponseEntity.ok(ApiResponse.ok("Shelter deleted", null));
    }
}
