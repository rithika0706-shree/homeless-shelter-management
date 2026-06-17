package com.tn.homeless.repository;

import com.tn.homeless.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    List<Shelter> findByVerified(boolean verified);

    List<Shelter> findByCity(String city);

    List<Shelter> findByNgoId(Long ngoId);

    /**
     * NEW QUERY — original had no capacity filter.
     * Finds shelters where available beds > 0 and that are verified.
     */
    @Query("SELECT s FROM Shelter s WHERE s.verified = true AND s.currentOccupancy < s.totalCapacity")
    List<Shelter> findAvailableShelters();

    /**
     * NEW QUERY — for dashboard stats.
     */
    @Query("SELECT COALESCE(SUM(s.totalCapacity), 0) FROM Shelter s WHERE s.verified = true")
    int sumTotalCapacity();

    @Query("SELECT COALESCE(SUM(s.currentOccupancy), 0) FROM Shelter s WHERE s.verified = true")
    int sumCurrentOccupancy();

    long countByVerified(boolean verified);
}
