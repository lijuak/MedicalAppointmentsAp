package com.medicalapp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medicalapp.model.Appointment;
import com.medicalapp.repository.AppointmentRepo;
import com.medicalapp.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepo.save(appointment);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepo.findAllAnimations();
    }

    private List<Appointment> findAllAnimations() {
        return appointmentRepo.findAllAppointments();
    }

    @Override
    public List<Appointment> filterAppointments(Integer urgencyLevel, Integer consultationFee, Integer durationMinutes, Integer clinicLocationId, Integer availabilityScore, Long patientId, Long id) {
        return appointmentRepo.filterAppointments(urgencyLevel, consultationFee, durationMinutes, clinicLocationId, availabilityScore, patientId, id);
    }

    @Override
    public void deleteById(Long id) {
        appointmentRepo.deleteById(id);
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepo.findById(id).orElse(null);
    }
}
