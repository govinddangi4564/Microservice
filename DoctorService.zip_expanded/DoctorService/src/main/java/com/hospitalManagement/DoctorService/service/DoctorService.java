package com.hospitalManagement.DoctorService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalManagement.DoctorService.entity.Doctor;
import com.hospitalManagement.DoctorService.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

	private final DoctorRepository repo;

	public Doctor save(Doctor d) {
		return repo.save(d);
	}

	public List<Doctor> getAll() {
		return repo.findAll();
	}

	public Doctor getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
	}

	public Doctor update(Long id, Doctor d) {

		Doctor existing = getById(id);

		existing.setName(d.getName());
		existing.setSpecialization(d.getSpecialization());
		existing.setEmail(d.getEmail());
		existing.setPhone(d.getPhone());

		return repo.save(existing);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}
