package com.hospitalManagement.AppointmentService.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

	private Long id;
	private DoctorDto doctor;
	private PatientDto patient;
	private LocalDate appointmentDate;
	private LocalTime appointmentTime;
	private String reason;
	private String status;
}