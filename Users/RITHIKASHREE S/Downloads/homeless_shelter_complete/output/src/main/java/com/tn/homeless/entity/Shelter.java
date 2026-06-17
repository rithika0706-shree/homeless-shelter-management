package com.tn.homeless.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Represents a shelter registered by an NGO and verified by an Admin.
 *
 * CHANGES from original:
 *  - Removed redundant Lombok annotations.
 *  - Added validation constraints.
 *  - Added 'contactPhone' and 'address' fields for complete shelter info.
 *  - Added 'createdAt' audit timestamp.
 *  - Added @Min(0) on capacity fields to prevent negative values.
 */
@Entity
@Table(name = "shelters")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shelter name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    private String zone;
    private String ward;

    @Column(length = 500)
    private String address;

    private String contactPhone;

    @Min(value = 1, message = "Total capacity must be at least 1")
    private int totalCapacity;

    @Min(value = 0, message = "Current occupancy cannot be negative")
    private int currentOccupancy;

    @Column(columnDefinition = "TEXT")
    private String facilities; // e.g., "Food,Medical,Beds,Clothing"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_user_id")
    private User ngo;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currentOccupancy < 0) {
            currentOccupancy = 0;
        }
    }

    // ---- Computed helper ----

    public int getAvailableCapacity() {
        return totalCapacity - currentOccupancy;
    }

    public boolean isFull() {
        return currentOccupancy >= totalCapacity;
    }

    // ---- Getters & Setters ----

    public Shelter() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public User getNgo() {
        return ngo;
    }

    public void setNgo(User ngo) {
        this.ngo = ngo;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
