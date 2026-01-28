package com.medicalapp.repository;

import com.medicalapp.model.Doctor;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE :specialty IS NULL OR d.specialty = :specialty")
    List<Doctor> findBySpecialty(@Param("specialty") String specialty);

}
