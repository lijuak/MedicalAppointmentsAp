package com.medicalapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medicalapp.model.Appointment;
import com.medicalapp.service.AppointmentService;
import com.medicalapp.dto.AppointmentFilterRequest;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*") // For development simplicity
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Appointment>> filterAppointments(@RequestBody AppointmentFilterRequest request) {
        List<Appointment> results = appointmentService.filterAppointments(
            request.getUrgencyLevel(),
            request.getConsultationFee(),
            request.getDurationMinutes(),
            request.getClinicLocationId(),
            request.getAvailabilityScore(),
            request.getPatientId(),
            request.getDoctorId(),
            request.getId()
        );
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        return new ResponseEntity<>(appointmentService.save(appointment), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.findById(id);
        if (appointment != null) {
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
