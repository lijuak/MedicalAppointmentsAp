package com.medicalapp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medicalapp.model.Doctor;
import com.medicalapp.repository.DoctorRepo;
import com.medicalapp.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepo doctorRepo;

    @Override
    public Doctor save(Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepo.findAll();
    }

    @Override
    public Doctor findById(Long id) {
        return doctorRepo.findById(id).orElse(null);
    }

    @Override
    public List<Doctor> findBySpecialty(String specialty) {
        return doctorRepo.findBySpecialty(specialty);
    }

    @Override
    public void deleteById(Long id) {
        doctorRepo.deleteById(id);
    }
}
