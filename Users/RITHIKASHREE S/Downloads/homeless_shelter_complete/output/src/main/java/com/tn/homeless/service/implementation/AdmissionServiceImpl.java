package com.tn.homeless.service.implementation;

import com.tn.homeless.dto.AdmissionRequestDto;
import com.tn.homeless.entity.Admission;
import com.tn.homeless.entity.HomelessPerson;
import com.tn.homeless.entity.Shelter;
import com.tn.homeless.entity.User;
import com.tn.homeless.exception.ResourceNotFoundException;
import com.tn.homeless.exception.ShelterFullException;
import com.tn.homeless.repository.AdmissionRepository;
import com.tn.homeless.repository.HomelessPersonRepository;
import com.tn.homeless.repository.ShelterRepository;
import com.tn.homeless.repository.UserRepository;
import com.tn.homeless.service.AdmissionService;
import com.tn.homeless.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * CHANGES from original:
 *  - Added capacity check before approving — original allowed over-booking.
 *  - Added currentOccupancy increment/decrement on approve/reject.
 *  - Integrated email notification on approve and reject.
 *  - Added duplicate-person check (can't raise two PENDING/APPROVED requests
 *    for the same homeless person simultaneously).
 *  - Added rejectAdmission() — was completely missing.
 *  - getAdmissionsByVolunteer() now takes username string, not raw ID.
 */
@Service
public class AdmissionServiceImpl implements AdmissionService {

    @Autowired
    private AdmissionRepository admissionRepository;

    @Autowired
    private HomelessPersonRepository homelessPersonRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Admission createAdmission(AdmissionRequestDto dto, String volunteerUsername) {

        HomelessPerson person = homelessPersonRepository.findById(dto.getHomelessPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("HomelessPerson", dto.getHomelessPersonId()));

        Shelter shelter = shelterRepository.findById(dto.getShelterId())
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", dto.getShelterId()));

        // Check shelter capacity before creating request
        if (shelter.isFull()) {
            throw new ShelterFullException(shelter.getName());
        }

        // Prevent duplicate active admission for same person
        boolean alreadyActive = admissionRepository
                .existsByHomelessPersonIdAndStatusIn(
                        person.getId(), List.of("PENDING", "APPROVED"));
        if (alreadyActive) {
            throw new IllegalArgumentException(
                    person.getName() + " already has an active or pending admission request.");
        }

        User volunteer = userRepository.findByUsername(volunteerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", null));

        Admission admission = new Admission();
        admission.setHomelessPerson(person);
        admission.setShelter(shelter);
        admission.setVolunteer(volunteer);
        admission.setStatus("PENDING");
        admission.setRemarks(dto.getRemarks());

        return admissionRepository.save(admission);
    }

    @Override
    @Transactional
    public Admission approveAdmission(Long id, String ngoUsername) {
        Admission admission = getAdmissionById(id);

        if (!"PENDING".equals(admission.getStatus())) {
            throw new IllegalArgumentException("Only PENDING admissions can be approved.");
        }

        Shelter shelter = admission.getShelter();
        if (shelter.isFull()) {
            throw new ShelterFullException(shelter.getName());
        }

        // Update occupancy
        shelter.setCurrentOccupancy(shelter.getCurrentOccupancy() + 1);
        shelterRepository.save(shelter);

        admission.setStatus("APPROVED");
        Admission saved = admissionRepository.save(admission);

        // Send notification email to volunteer
        String volunteerEmail = admission.getVolunteer() != null
                ? admission.getVolunteer().getEmail() : null;
        emailService.sendAdmissionApprovedEmail(
                volunteerEmail,
                admission.getHomelessPerson().getName(),
                shelter.getName());

        return saved;
    }

    @Override
    @Transactional
    public Admission rejectAdmission(Long id, String ngoUsername, String reason) {
        Admission admission = getAdmissionById(id);

        if (!"PENDING".equals(admission.getStatus())) {
            throw new IllegalArgumentException("Only PENDING admissions can be rejected.");
        }

        admission.setStatus("REJECTED");
        admission.setRejectionReason(reason);
        Admission saved = admissionRepository.save(admission);

        // Send notification email to volunteer
        String volunteerEmail = admission.getVolunteer() != null
                ? admission.getVolunteer().getEmail() : null;
        emailService.sendAdmissionRejectedEmail(
                volunteerEmail,
                admission.getHomelessPerson().getName(),
                reason);

        return saved;
    }

    @Override
    public List<Admission> getAllAdmissions() {
        return admissionRepository.findAll();
    }

    @Override
    public List<Admission> getPendingAdmissions() {
        return admissionRepository.findByStatus("PENDING");
    }

    @Override
    public List<Admission> getAdmissionsByVolunteer(String volunteerUsername) {
        User volunteer = userRepository.findByUsername(volunteerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", null));
        return admissionRepository.findByVolunteerId(volunteer.getId());
    }

    @Override
    public Admission getAdmissionById(Long id) {
        return admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission", id));
    }
}
