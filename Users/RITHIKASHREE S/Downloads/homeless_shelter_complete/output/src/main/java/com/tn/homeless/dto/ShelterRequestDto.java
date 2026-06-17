package com.tn.homeless.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for NGO to register a new shelter.
 *
 * WHY NEEDED:
 *  - The original ShelterController accepted a raw Shelter entity in the request body,
 *    which exposes internal fields like 'verified', 'ngo', and 'currentOccupancy'
 *    to direct manipulation from the client — a security risk.
 *  - This DTO accepts only what the NGO is allowed to set.
 */
public class ShelterRequestDto {

    @NotBlank(message = "Shelter name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    private String zone;
    private String ward;
    private String address;
    private String contactPhone;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer totalCapacity;

    private String facilities; // comma-separated: "Food,Medical,Beds"

    public ShelterRequestDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Integer getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
}
