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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointments")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
