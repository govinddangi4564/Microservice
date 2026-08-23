package com.hospitalManagement.PatientService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalManagement.PatientService.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
