package com.tn.homeless.controller;

import com.tn.homeless.dto.ApiResponse;
import com.tn.homeless.entity.HomelessPerson;
import com.tn.homeless.exception.ResourceNotFoundException;
import com.tn.homeless.repository.HomelessPersonRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * CRUD endpoints for HomelessPerson records.
 *
 * WHY NEEDED:
 *  This controller was completely absent from the original project.
 *  Without it, volunteers had no API to register a homeless person,
 *  and the AdmissionController's createAdmission() had no person IDs
 *  to reference — making the entire admission flow impossible.
 *
 * ROLE RULES:
 *  - VOLUNTEER / ADMIN  → can register and view persons
 *  - ADMIN              → can delete
 */
@RestController
@RequestMapping("/persons")
public class HomelessPersonController {

    @Autowired
    private HomelessPersonRepository homelessPersonRepository;

    /** GET /persons — all roles can view */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HomelessPerson>>> getAllPersons() {
        return ResponseEntity.ok(
                ApiResponse.ok("All homeless persons", homelessPersonRepository.findAll()));
    }

    /** GET /persons/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomelessPerson>> getById(@PathVariable Long id) {
        HomelessPerson person = homelessPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HomelessPerson", id));
        return ResponseEntity.ok(ApiResponse.ok("Homeless person details", person));
    }

    /** GET /persons/search?name=John */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HomelessPerson>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok("Search results",
                homelessPersonRepository.findByNameContainingIgnoreCase(name)));
    }

    /** POST /persons — volunteer registers a new homeless person */
    @PostMapping
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<HomelessPerson>> register(
            @Valid @RequestBody HomelessPerson person) {
        HomelessPerson saved = homelessPersonRepository.save(person);
        return ResponseEntity.ok(ApiResponse.ok("Homeless person registered successfully", saved));
    }

    /** PUT /persons/{id} — update details */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<HomelessPerson>> update(
            @PathVariable Long id, @Valid @RequestBody HomelessPerson updated) {
        HomelessPerson existing = homelessPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HomelessPerson", id));
        existing.setName(updated.getName());
        existing.setAge(updated.getAge());
        existing.setGender(updated.getGender());
        existing.setHealthConditions(updated.getHealthConditions());
        existing.setSpecialNeeds(updated.isSpecialNeeds());
        existing.setLocation(updated.getLocation());
        return ResponseEntity.ok(ApiResponse.ok("Record updated",
                homelessPersonRepository.save(existing)));
    }

    /** DELETE /persons/{id} — ADMIN only */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        homelessPersonRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Record deleted", null));
    }
}
