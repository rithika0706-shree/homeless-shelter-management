package com.tn.homeless.repository;

import com.tn.homeless.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    List<Admission> findByStatus(String status);

    List<Admission> findByVolunteerId(Long volunteerId);

    List<Admission> findByShelterId(Long shelterId);

    /**
     * NEW QUERY — needed to prevent double admission of same person.
     * If a person is already in PENDING or APPROVED state, don't allow
     * another admission request.
     */
    boolean existsByHomelessPersonIdAndStatusIn(Long personId, List<String> statuses);

    long countByStatus(String status);
}
