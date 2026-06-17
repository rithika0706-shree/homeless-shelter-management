package com.tn.homeless.service;

public interface EmailService {
    void sendAdmissionApprovedEmail(String toEmail, String personName, String shelterName);
    void sendAdmissionRejectedEmail(String toEmail, String personName, String reason);
    void sendCapacityAlertEmail(String toAdminEmail, String shelterName, int used, int total);
}
