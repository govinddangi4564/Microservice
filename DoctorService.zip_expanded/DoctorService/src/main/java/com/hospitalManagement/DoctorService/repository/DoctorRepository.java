package com.hospitalManagement.DoctorService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalManagement.DoctorService.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
