package com.medicalapp.service;

import java.util.Optional;
import com.medicalapp.model.Patient;

public interface PatientService {
    
    Patient save(Patient patient);
    
    Optional<Patient> findByEmail(String email);
    
    boolean existsByEmail(String email);

    Patient findOrCreatePatient(String uid, String email, String name);
}
