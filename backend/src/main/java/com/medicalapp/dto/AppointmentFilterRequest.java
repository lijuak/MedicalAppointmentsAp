package com.medicalapp.dto;

import lombok.Data;

@Data
public class AppointmentFilterRequest {
    private Integer urgencyLevel;
    private Integer consultationFee;
    private Integer durationMinutes;
    private Integer clinicLocationId;
    private Integer availabilityScore;
    private Long patientId;
    private Long id;
}
