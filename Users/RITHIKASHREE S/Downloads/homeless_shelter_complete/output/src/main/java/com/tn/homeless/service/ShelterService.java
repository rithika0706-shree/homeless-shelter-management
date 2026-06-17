package com.tn.homeless.service;

import com.tn.homeless.entity.Shelter;
import java.util.List;

public interface ShelterService {
    Shelter addShelter(Shelter shelter, String ngoUsername);
    Shelter verifyShelter(Long id);
    List<Shelter> getAllShelters();
    List<Shelter> getVerifiedShelters();
    List<Shelter> getAvailableShelters();
    Shelter getShelterById(Long id);
    Shelter updateShelter(Long id, Shelter updated);
    void deleteShelter(Long id);
}
