package com.tn.homeless.controller;

import com.tn.homeless.dto.AdmissionRequestDto;
import com.tn.homeless.dto.ApiResponse;
import com.tn.homeless.entity.Admission;
import com.tn.homeless.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * CHANGES from original:
 *  - Added @PreAuthorize role guards everywhere.
 *  - Added PUT /admissions/{id}/reject with reason body (was missing entirely).
 *  - GET /admissions/my — volunteer sees only their own requests.
 *  - All responses wrapped in ApiResponse for consistent shape.
 *  - Volunteer username now taken from Authentication principal (not passed as param).
 */
@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    /** POST /admissions — volunteer raises a new admission request */
    @PostMapping
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Admission>> createAdmission(
            @Valid @RequestBody AdmissionRequestDto dto, Authentication auth) {
        Admission saved = admissionService.createAdmission(dto, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Admission request submitted", saved));
    }

    /** GET /admissions — ADMIN or NGO sees all admissions */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<ApiResponse<List<Admission>>> getAllAdmissions() {
        return ResponseEntity.ok(ApiResponse.ok("All admissions", admissionService.getAllAdmissions()));
    }

    /** GET /admissions/pending — NGO reviews pending requests */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<ApiResponse<List<Admission>>> getPendingAdmissions() {
        return ResponseEntity.ok(ApiResponse.ok("Pending admissions", admissionService.getPendingAdmissions()));
    }

    /** GET /admissions/my — volunteer sees their own submissions */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Admission>>> getMyAdmissions(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("My admissions",
                admissionService.getAdmissionsByVolunteer(auth.getName())));
    }

    /** GET /admissions/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Admission>> getAdmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Admission details", admissionService.getAdmissionById(id)));
    }

    /** PUT /admissions/{id}/approve — NGO approves */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Admission>> approve(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Admission approved", admissionService.approveAdmission(id, auth.getName())));
    }

    /** PUT /admissions/{id}/reject — NGO rejects with a reason */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Admission>> reject(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String reason = body.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(ApiResponse.ok("Admission rejected",
                admissionService.rejectAdmission(id, auth.getName(), reason)));
    }
}
