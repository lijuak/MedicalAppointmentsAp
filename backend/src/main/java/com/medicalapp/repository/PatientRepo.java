package com.medicalapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.medicalapp.model.Patient;

public interface PatientRepo extends JpaRepository<Patient, Long> {

    boolean existsByEmail(String email);
    
    Optional<Patient> findByEmail(String email);
}
