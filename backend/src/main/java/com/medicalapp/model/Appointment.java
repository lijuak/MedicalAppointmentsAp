package com.medicalapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String doctorName; // was titulo

    @Column
    String specialty; // was descripcion

    @Column
    Integer urgencyLevel; // was temporada (1-5)

    @Column
    Integer consultationFee; // was dinero

    @Column
    Integer durationMinutes; // was intensidad

    @Column
    Integer clinicLocationId; // was cercania

    @Column
    Integer availabilityScore; // was facilidad

    @Column(name = "is_insurance_covered")
    boolean isInsuranceCovered; // was esDefault

    @Column(name = "created_at")
    LocalDateTime createdAt; // was fechaCreacion

    @Column(name = "patient_id")
    private Long patientId; // was creadorId

    @Column(name = "appointment_date_time")
    LocalDateTime appointmentDateTime; // Fecha y hora de la cita

    @Column(name = "patient_symptoms", length = 1000)
    String patientSymptoms; // Descripción del problema del paciente

    @Column(name = "doctor_id")
    Long doctorId; // ID del médico asignado

    @Column(nullable = false)
    private String status = "RESERVADA"; // Estado de la cita

    public Appointment() {}

    public Appointment(Long id, String doctorName, String specialty, Integer urgencyLevel, Integer consultationFee, Integer durationMinutes, Integer clinicLocationId, Integer availabilityScore, boolean isInsuranceCovered, LocalDateTime createdAt, Long patientId, LocalDateTime appointmentDateTime, String patientSymptoms, Long doctorId, String status) {
        this.id = id;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.urgencyLevel = urgencyLevel;
        this.consultationFee = consultationFee;
        this.durationMinutes = durationMinutes;
        this.clinicLocationId = clinicLocationId;
        this.availabilityScore = availabilityScore;
        this.isInsuranceCovered = isInsuranceCovered;
        this.createdAt = createdAt;
        this.patientId = patientId;
        this.appointmentDateTime = appointmentDateTime;
        this.patientSymptoms = patientSymptoms;
        this.doctorId = doctorId;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

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

    public boolean isInsuranceCovered() { return isInsuranceCovered; }
    public void setInsuranceCovered(boolean insuranceCovered) { isInsuranceCovered = insuranceCovered; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getPatientSymptoms() { return patientSymptoms; }
    public void setPatientSymptoms(String patientSymptoms) { this.patientSymptoms = patientSymptoms; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
