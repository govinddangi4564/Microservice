package com.hospitalManagement.AppointmentService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
	private Long id;
	private String name;
	private String specialization;
	private String email;
	private String phone;
}
