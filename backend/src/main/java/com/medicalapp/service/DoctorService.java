package com.medicalapp.service;

import java.util.List;
import com.medicalapp.model.Doctor;

public interface DoctorService {
    Doctor save(Doctor doctor);
    List<Doctor> findAll();
    Doctor findById(Long id);
    List<Doctor> findBySpecialty(String specialty);
    void deleteById(Long id);
}
