package com.tn.homeless.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdmissionRequestDto {

    @NotNull(message = "Homeless person ID is required")
    @Min(value = 1, message = "Invalid homeless person ID")
    private Long homelessPersonId;

    @NotNull(message = "Shelter ID is required")
    @Min(value = 1, message = "Invalid shelter ID")
    private Long shelterId;

    private String remarks;

    public AdmissionRequestDto() {}
    public Long getHomelessPersonId() { return homelessPersonId; }
    public void setHomelessPersonId(Long homelessPersonId) { this.homelessPersonId = homelessPersonId; }
    public Long getShelterId() { return shelterId; }
    public void setShelterId(Long shelterId) { this.shelterId = shelterId; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
