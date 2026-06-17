package com.tn.homeless.dto;

/**
 * NEW DTO — was completely missing from the original project.
 * DashboardController was returning raw Maps which is error-prone.
 * This typed DTO makes the response strongly-typed and self-documenting.
 */
public class DashboardStatsDto {

    private long totalShelters;
    private long verifiedShelters;
    private long totalAdmissions;
    private long pendingAdmissions;
    private long approvedAdmissions;
    private long rejectedAdmissions;
    private long totalHomelessPersons;
    private long totalUsers;
    private int totalCapacity;
    private int currentOccupancy;

    public DashboardStatsDto() {}

    public long getTotalShelters() { return totalShelters; }
    public void setTotalShelters(long totalShelters) { this.totalShelters = totalShelters; }
    public long getVerifiedShelters() { return verifiedShelters; }
    public void setVerifiedShelters(long verifiedShelters) { this.verifiedShelters = verifiedShelters; }
    public long getTotalAdmissions() { return totalAdmissions; }
    public void setTotalAdmissions(long totalAdmissions) { this.totalAdmissions = totalAdmissions; }
    public long getPendingAdmissions() { return pendingAdmissions; }
    public void setPendingAdmissions(long pendingAdmissions) { this.pendingAdmissions = pendingAdmissions; }
    public long getApprovedAdmissions() { return approvedAdmissions; }
    public void setApprovedAdmissions(long approvedAdmissions) { this.approvedAdmissions = approvedAdmissions; }
    public long getRejectedAdmissions() { return rejectedAdmissions; }
    public void setRejectedAdmissions(long rejectedAdmissions) { this.rejectedAdmissions = rejectedAdmissions; }
    public long getTotalHomelessPersons() { return totalHomelessPersons; }
    public void setTotalHomelessPersons(long totalHomelessPersons) { this.totalHomelessPersons = totalHomelessPersons; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }
    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }
}
