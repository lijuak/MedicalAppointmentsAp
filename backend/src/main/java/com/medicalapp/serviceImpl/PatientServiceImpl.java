package com.medicalapp.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medicalapp.model.Patient;
import com.medicalapp.repository.PatientRepo;
import com.medicalapp.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepo patientRepo;

    @Override
    public Patient save(Patient patient) {
        return patientRepo.save(patient);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return patientRepo.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return patientRepo.existsByEmail(email);
    }

    @Override
    public Patient findOrCreatePatient(String uid, String email, String name) {
        Optional<Patient> existing = patientRepo.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        Patient newPatient = new Patient();
        newPatient.setEmail(email);
        newPatient.setNombre(name);
        newPatient.setUsername(email); // Set email as username to satisfy nullable = false
        newPatient.setPassword("firebase_oauth"); // Placeholder
        return patientRepo.save(newPatient);
    }
}
