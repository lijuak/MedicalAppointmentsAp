package com.medicalapp.dto;

import lombok.Data;

public class AppointmentFilterRequest {
    private Integer urgencyLevel;
    private Integer consultationFee;
    private Integer durationMinutes;
    private Integer clinicLocationId;
    private Integer availabilityScore;
    private Long patientId;
    private Long doctorId;
    private Long id;

    public Integer getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(Integer urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public Integer getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Integer consultationFee) { this.consultationFee = consultationFee; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getClinicLocationId() { return clinicLocationId; }
    public void setClinicLocationId(Integer clinicLocationId) { this.clinicLocationId = clinicLocationId; }

    public Integer getAvailabilityScore() { return availabilityScore; }
    public void setAvailabilityScore(Integer availabilityScore) { this.availabilityScore = availabilityScore; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
