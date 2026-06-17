package com.tn.homeless.service.implementation;

import com.tn.homeless.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * CHANGES from original:
 *  - Original had a single sendMail(String to, String subject, String body) method
 *    with no meaningful callers — callers were hard-coded with empty strings.
 *  - Replaced with three domain-specific methods so each use case sends
 *    a properly formatted, meaningful email.
 *  - Added try/catch so a mail failure does not crash the main transaction.
 */
@Service
public class EmailServiceImple implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendAdmissionApprovedEmail(String toEmail, String personName, String shelterName) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject("Admission Approved – Homeless Shelter System");
            msg.setText("Dear Volunteer,\n\n" +
                    "The admission request for " + personName +
                    " has been APPROVED and they will be placed at " + shelterName + ".\n\n" +
                    "Thank you for your service.\n\nHomeless Shelter Management System");
            mailSender.send(msg);
        } catch (Exception e) {
            // Log and continue; email failure must not roll back the admission
            System.err.println("[EmailService] Failed to send approval email: " + e.getMessage());
        }
    }

    @Override
    public void sendAdmissionRejectedEmail(String toEmail, String personName, String reason) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject("Admission Rejected – Homeless Shelter System");
            msg.setText("Dear Volunteer,\n\n" +
                    "The admission request for " + personName +
                    " has been REJECTED.\nReason: " + reason + "\n\n" +
                    "Please try a different shelter or contact the admin.\n\nHomeless Shelter Management System");
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send rejection email: " + e.getMessage());
        }
    }

    @Override
    public void sendCapacityAlertEmail(String toAdminEmail, String shelterName, int used, int total) {
        if (toAdminEmail == null || toAdminEmail.isBlank()) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toAdminEmail);
            msg.setSubject("⚠ Shelter Capacity Alert – " + shelterName);
            msg.setText("Admin Alert,\n\n" +
                    "Shelter '" + shelterName + "' is at " + used + "/" + total +
                    " capacity (" + (used * 100 / total) + "%).\n" +
                    "Please consider increasing capacity or opening a new shelter.\n\n" +
                    "Homeless Shelter Management System");
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send capacity alert: " + e.getMessage());
        }
    }
}
