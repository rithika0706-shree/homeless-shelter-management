package com.tn.homeless;

import com.tn.homeless.dto.AdmissionRequestDto;
import com.tn.homeless.entity.*;
import com.tn.homeless.exception.ResourceNotFoundException;
import com.tn.homeless.exception.ShelterFullException;
import com.tn.homeless.repository.*;
import com.tn.homeless.service.EmailService;
import com.tn.homeless.service.implementation.AdmissionServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdmissionServiceImpl using Mockito.
 *
 * TEST CASES:
 *  TC-06  Create admission with valid data       → Admission saved, status=PENDING
 *  TC-07  Create admission for full shelter      → ShelterFullException thrown
 *  TC-08  Create duplicate active admission      → IllegalArgumentException thrown
 *  TC-09  Approve PENDING admission              → status=APPROVED, occupancy +1
 *  TC-10  Reject PENDING admission               → status=REJECTED, email sent
 *  TC-11  Approve already-APPROVED admission     → IllegalArgumentException thrown
 *  TC-12  Get admission by non-existent ID       → ResourceNotFoundException thrown
 */
@ExtendWith(MockitoExtension.class)
class AdmissionServiceTest {

    @InjectMocks AdmissionServiceImpl service;
    @Mock AdmissionRepository admissionRepo;
    @Mock HomelessPersonRepository personRepo;
    @Mock ShelterRepository shelterRepo;
    @Mock UserRepository userRepo;
    @Mock EmailService emailService;

    private HomelessPerson makePerson(Long id, String name) {
        HomelessPerson p = new HomelessPerson();
        p.setId(id); p.setName(name); p.setAge(30); p.setGender("MALE");
        return p;
    }
    private Shelter makeShelter(Long id, int cap, int occ, boolean verified) {
        Shelter s = new Shelter();
        s.setId(id); s.setName("Test Shelter"); s.setCity("Chennai");
        s.setTotalCapacity(cap); s.setCurrentOccupancy(occ);
        s.setVerified(verified);
        return s;
    }
    private User makeUser(Long id, String username) {
        User u = new User(); u.setId(id); u.setUsername(username);
        u.setEmail(username + "@test.com"); return u;
    }

    /* ── TC-06 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-06: Create admission → PENDING saved")
    void createAdmission_success() {
        HomelessPerson person = makePerson(1L, "Rajan");
        Shelter shelter = makeShelter(1L, 20, 5, true);
        User vol = makeUser(3L, "vol1");

        when(personRepo.findById(1L)).thenReturn(Optional.of(person));
        when(shelterRepo.findById(1L)).thenReturn(Optional.of(shelter));
        when(admissionRepo.existsByHomelessPersonIdAndStatusIn(eq(1L), anyList())).thenReturn(false);
        when(userRepo.findByUsername("vol1")).thenReturn(Optional.of(vol));
        when(admissionRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        AdmissionRequestDto dto = new AdmissionRequestDto();
        dto.setHomelessPersonId(1L); dto.setShelterId(1L); dto.setRemarks("Urgent");

        Admission result = service.createAdmission(dto, "vol1");

        assertEquals("PENDING", result.getStatus());
        verify(admissionRepo).save(any(Admission.class));
    }

    /* ── TC-07 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-07: Shelter full → ShelterFullException")
    void createAdmission_shelterFull() {
        HomelessPerson person = makePerson(2L, "Meena");
        Shelter shelter = makeShelter(1L, 10, 10, true);  // full

        when(personRepo.findById(2L)).thenReturn(Optional.of(person));
        when(shelterRepo.findById(1L)).thenReturn(Optional.of(shelter));

        AdmissionRequestDto dto = new AdmissionRequestDto();
        dto.setHomelessPersonId(2L); dto.setShelterId(1L);

        assertThrows(ShelterFullException.class, () -> service.createAdmission(dto, "vol1"));
        verify(admissionRepo, never()).save(any());
    }

    /* ── TC-08 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-08: Duplicate active admission → IllegalArgumentException")
    void createAdmission_duplicate() {
        HomelessPerson person = makePerson(3L, "Arjun");
        Shelter shelter = makeShelter(1L, 20, 5, true);

        when(personRepo.findById(3L)).thenReturn(Optional.of(person));
        when(shelterRepo.findById(1L)).thenReturn(Optional.of(shelter));
        when(admissionRepo.existsByHomelessPersonIdAndStatusIn(eq(3L), anyList())).thenReturn(true);

        AdmissionRequestDto dto = new AdmissionRequestDto();
        dto.setHomelessPersonId(3L); dto.setShelterId(1L);

        assertThrows(IllegalArgumentException.class, () -> service.createAdmission(dto, "vol1"));
    }

    /* ── TC-09 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-09: Approve PENDING admission → APPROVED, occupancy+1")
    void approveAdmission_success() {
        Shelter shelter = makeShelter(1L, 20, 5, true);
        Admission admission = new Admission();
        admission.setId(1L); admission.setStatus("PENDING");
        admission.setShelter(shelter); admission.setHomelessPerson(makePerson(1L, "Rajan"));
        admission.setVolunteer(makeUser(3L, "vol1"));

        when(admissionRepo.findById(1L)).thenReturn(Optional.of(admission));
        when(shelterRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(admissionRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Admission result = service.approveAdmission(1L, "ngo_hope");

        assertEquals("APPROVED", result.getStatus());
        assertEquals(6, shelter.getCurrentOccupancy());
        verify(emailService).sendAdmissionApprovedEmail(anyString(), anyString(), anyString());
    }

    /* ── TC-10 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-10: Reject PENDING admission → REJECTED, email sent")
    void rejectAdmission_success() {
        Admission admission = new Admission();
        admission.setId(2L); admission.setStatus("PENDING");
        admission.setShelter(makeShelter(1L, 20, 5, true));
        admission.setHomelessPerson(makePerson(2L, "Meena"));
        admission.setVolunteer(makeUser(3L, "vol1"));

        when(admissionRepo.findById(2L)).thenReturn(Optional.of(admission));
        when(admissionRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Admission result = service.rejectAdmission(2L, "ngo_hope", "Shelter not equipped");

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Shelter not equipped", result.getRejectionReason());
        verify(emailService).sendAdmissionRejectedEmail(anyString(), anyString(), eq("Shelter not equipped"));
    }

    /* ── TC-11 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-11: Approve already-APPROVED admission → IllegalArgumentException")
    void approveAdmission_alreadyApproved() {
        Admission admission = new Admission();
        admission.setId(3L); admission.setStatus("APPROVED");

        when(admissionRepo.findById(3L)).thenReturn(Optional.of(admission));

        assertThrows(IllegalArgumentException.class,
                () -> service.approveAdmission(3L, "ngo_hope"));
    }

    /* ── TC-12 ─────────────────────────────────────────────────── */
    @Test @DisplayName("TC-12: Get admission by non-existent ID → ResourceNotFoundException")
    void getAdmissionById_notFound() {
        when(admissionRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getAdmissionById(999L));
    }
}
