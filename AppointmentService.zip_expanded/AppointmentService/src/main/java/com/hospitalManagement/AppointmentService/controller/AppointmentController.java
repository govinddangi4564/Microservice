package com.hospitalManagement.AppointmentService.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalManagement.AppointmentService.dto.DoctorDto;
import com.hospitalManagement.AppointmentService.dto.PatientDto;
import com.hospitalManagement.AppointmentService.entity.Appointment;
import com.hospitalManagement.AppointmentService.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentService service;

	@PostMapping
	public Appointment create(@RequestBody Appointment ap) {
		return service.create(ap);
	}

	@GetMapping
	public List<Appointment> getAll() {
		return service.getAll();
	}

	@GetMapping("/{id}")
	public Appointment getById(@PathVariable Long id) {
		return service.getById(id);
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable Long id) {
		service.delete(id);
		return "Appointment deleted successfully";
	}

	@GetMapping("/doctor/{doctorId}")
	public DoctorDto getDoctor(@PathVariable Long doctorId) {
		return service.getDoctor(doctorId);
	}

	@GetMapping("/patient/{patientId}")
	public PatientDto getPatient(@PathVariable Long patientId) {
		return service.getPatient(patientId);
	}

}
