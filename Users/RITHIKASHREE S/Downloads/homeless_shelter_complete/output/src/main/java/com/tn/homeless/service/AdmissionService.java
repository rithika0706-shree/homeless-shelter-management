package com.tn.homeless.service;

import com.tn.homeless.dto.AdmissionRequestDto;
import com.tn.homeless.entity.Admission;
import java.util.List;

public interface AdmissionService {
    Admission createAdmission(AdmissionRequestDto dto, String volunteerUsername);
    Admission approveAdmission(Long id, String ngoUsername);
    Admission rejectAdmission(Long id, String ngoUsername, String reason);
    List<Admission> getAllAdmissions();
    List<Admission> getPendingAdmissions();
    List<Admission> getAdmissionsByVolunteer(String volunteerUsername);
    Admission getAdmissionById(Long id);
}
