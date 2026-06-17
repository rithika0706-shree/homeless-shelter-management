package com.tn.homeless.repository;

import com.tn.homeless.entity.HomelessPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HomelessPersonRepository extends JpaRepository<HomelessPerson, Long> {
    List<HomelessPerson> findByNameContainingIgnoreCase(String name);
    List<HomelessPerson> findByGender(String gender);
    List<HomelessPerson> findBySpecialNeeds(boolean specialNeeds);
}
