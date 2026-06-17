package com.tn.homeless.controller;

import com.tn.homeless.dto.ApiResponse;
import com.tn.homeless.dto.DashboardStatsDto;
import com.tn.homeless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Returns aggregate statistics for the admin dashboard.
 *
 * CHANGES from original:
 *  - Original returned a raw Map<String, Object> with hardcoded 0 values.
 *  - Now uses typed DashboardStatsDto populated from real DB queries.
 *  - Access restricted to ADMIN role.
 */
@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired private ShelterRepository shelterRepository;
    @Autowired private AdmissionRepository admissionRepository;
    @Autowired private HomelessPersonRepository homelessPersonRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalShelters(shelterRepository.count());
        stats.setVerifiedShelters(shelterRepository.countByVerified(true));
        stats.setTotalAdmissions(admissionRepository.count());
        stats.setPendingAdmissions(admissionRepository.countByStatus("PENDING"));
        stats.setApprovedAdmissions(admissionRepository.countByStatus("APPROVED"));
        stats.setRejectedAdmissions(admissionRepository.countByStatus("REJECTED"));
        stats.setTotalHomelessPersons(homelessPersonRepository.count());
        stats.setTotalUsers(userRepository.count());
        stats.setTotalCapacity(shelterRepository.sumTotalCapacity());
        stats.setCurrentOccupancy(shelterRepository.sumCurrentOccupancy());

        return ResponseEntity.ok(ApiResponse.ok("Dashboard statistics", stats));
    }
}
