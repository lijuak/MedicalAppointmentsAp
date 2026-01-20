package com.medicalapp.repository;

import com.medicalapp.model.Appointment;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

        @Query("SELECT a FROM Appointment a " +
                        "WHERE (:urgencyLevel IS NULL OR a.urgencyLevel = :urgencyLevel) " +
                        "AND (:consultationFee IS NULL OR a.consultationFee = :consultationFee) " +
                        "AND (:durationMinutes IS NULL OR a.durationMinutes = :durationMinutes) " +
                        "AND (:clinicLocationId IS NULL OR a.clinicLocationId = :clinicLocationId) " +
                        "AND (:availabilityScore IS NULL OR a.availabilityScore = :availabilityScore) " +
                        "AND (:patientId IS NULL OR a.patientId = :patientId) " +
                        "AND (:id IS NULL OR a.id = :id)")
        List<Appointment> filterAppointments(
                        @Param("urgencyLevel") Integer urgencyLevel,
                        @Param("consultationFee") Integer consultationFee,
                        @Param("durationMinutes") Integer durationMinutes,
                        @Param("clinicLocationId") Integer clinicLocationId,
                        @Param("availabilityScore") Integer availabilityScore,
                        @Param("patientId") Long patientId,
                        @Param("id") Long id);

}
