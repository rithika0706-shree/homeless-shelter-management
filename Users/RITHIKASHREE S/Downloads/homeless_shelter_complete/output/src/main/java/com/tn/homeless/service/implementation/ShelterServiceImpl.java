package com.tn.homeless.service.implementation;

import com.tn.homeless.entity.Shelter;
import com.tn.homeless.entity.User;
import com.tn.homeless.exception.ResourceNotFoundException;
import com.tn.homeless.repository.ShelterRepository;
import com.tn.homeless.repository.UserRepository;
import com.tn.homeless.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * CHANGES from original:
 *  - Added getAvailableShelters() — original was missing, causing the volunteer
 *    admission form to show full/unverified shelters.
 *  - Added updateShelter() and deleteShelter() — original only had add+verify.
 *  - Added ResourceNotFoundException instead of null returns.
 *  - addShelter now links the NGO user from their username (principal).
 */
@Service
public class ShelterServiceImpl implements ShelterService {

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Shelter addShelter(Shelter shelter, String ngoUsername) {
        User ngo = userRepository.findByUsername(ngoUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", null));
        shelter.setNgo(ngo);
        shelter.setVerified(false);
        return shelterRepository.save(shelter);
    }

    @Override
    @Transactional
    public Shelter verifyShelter(Long id) {
        Shelter shelter = getShelterById(id);
        shelter.setVerified(true);
        return shelterRepository.save(shelter);
    }

    @Override
    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    @Override
    public List<Shelter> getVerifiedShelters() {
        return shelterRepository.findByVerified(true);
    }

    @Override
    public List<Shelter> getAvailableShelters() {
        return shelterRepository.findAvailableShelters();
    }

    @Override
    public Shelter getShelterById(Long id) {
        return shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", id));
    }

    @Override
    @Transactional
    public Shelter updateShelter(Long id, Shelter updated) {
        Shelter existing = getShelterById(id);
        existing.setName(updated.getName());
        existing.setCity(updated.getCity());
        existing.setZone(updated.getZone());
        existing.setWard(updated.getWard());
        existing.setAddress(updated.getAddress());
        existing.setContactPhone(updated.getContactPhone());
        existing.setTotalCapacity(updated.getTotalCapacity());
        existing.setFacilities(updated.getFacilities());
        return shelterRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteShelter(Long id) {
        Shelter shelter = getShelterById(id);
        shelterRepository.delete(shelter);
    }
}
