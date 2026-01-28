package com.medicalapp.service;

import java.util.List;
import com.medicalapp.model.Appointment;

public interface AppointmentService {

    Appointment save(Appointment appointment);

    List<Appointment> findAll();

    List<Appointment> filterAppointments(Integer urgencyLevel, Integer consultationFee, Integer durationMinutes, Integer clinicLocationId, Integer availabilityScore, Long patientId, Long doctorId, Long id);

    void deleteById(Long id);
    
    Appointment findById(Long id);
}
