package com.tn.homeless.Scheduler;

import com.tn.homeless.entity.Shelter;
import com.tn.homeless.entity.User;
import com.tn.homeless.repository.ShelterRepository;
import com.tn.homeless.repository.UserRepository;
import com.tn.homeless.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Scheduled job that runs every day at 8:00 AM and sends capacity
 * alert emails for shelters at or above 80% occupancy.
 *
 * CHANGES from original:
 *  - Original EmailScheduler had a completely empty run() method.
 *  - Added full capacity-check logic querying the DB.
 *  - Sends alert to NGO owner; falls back to first Admin email.
 *  - @EnableScheduling was added to HomelessShelterApplication to activate this.
 */
@Component
public class EmailScheduler {

    @Autowired private ShelterRepository shelterRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;

    private static final double ALERT_THRESHOLD = 0.80;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendCapacityAlerts() {
        System.out.println("[EmailScheduler] Running daily capacity alert check...");
        List<Shelter> shelters = shelterRepository.findByVerified(true);
        for (Shelter shelter : shelters) {
            if (shelter.getTotalCapacity() == 0) continue;
            double usage = (double) shelter.getCurrentOccupancy() / shelter.getTotalCapacity();
            if (usage >= ALERT_THRESHOLD) {
                String email = (shelter.getNgo() != null) ? shelter.getNgo().getEmail() : null;
                if (email == null || email.isBlank()) {
                    email = userRepository.findAll().stream()
                            .filter(u -> u.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName())))
                            .map(User::getEmail).filter(e -> e != null && !e.isBlank())
                            .findFirst().orElse(null);
                }
                if (email != null) {
                    emailService.sendCapacityAlertEmail(email, shelter.getName(),
                            shelter.getCurrentOccupancy(), shelter.getTotalCapacity());
                }
            }
        }
        System.out.println("[EmailScheduler] Done.");
    }
}
