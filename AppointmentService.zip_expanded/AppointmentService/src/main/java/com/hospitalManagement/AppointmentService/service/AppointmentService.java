package com.hospitalManagement.AppointmentService.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hospitalManagement.AppointmentService.dto.AppointmentResponseDto;
import com.hospitalManagement.AppointmentService.dto.DoctorDto;
import com.hospitalManagement.AppointmentService.dto.PatientDto;
import com.hospitalManagement.AppointmentService.entity.Appointment;
import com.hospitalManagement.AppointmentService.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

	private final AppointmentRepository repo;

	// RestTemplate
//	private final RestTemplate restTemplate;

	// WebClient
	private final WebClient.Builder webClientBuilder;

//	private static final String PATIENT_SERVICE = "http://localhost:8081/patient/";
//	private static final String DOCTOR_SERVICE = "http://localhost:8082/doctor/";

	private static final String PATIENT_SERVICE = "http://patientService/patient/";

	private static final String DOCTOR_SERVICE = "http://doctorService/doctor/";

	public Appointment create(Appointment ap) {

//		DoctorDto doctor = restTemplate.getForObject(DOCTOR_SERVICE + ap.getDoctorId(), DoctorDto.class);

		DoctorDto doctor = webClientBuilder.build().get().uri(DOCTOR_SERVICE + ap.getDoctorId()).retrieve()
				.bodyToMono(DoctorDto.class).block();

		if (doctor == null) {
			throw new RuntimeException("Doctor not found");
		}

//		PatientDto patient = restTemplate.getForObject(PATIENT_SERVICE + ap.getPatientId(), PatientDto.class);

		PatientDto patient = webClientBuilder.build().get().uri(PATIENT_SERVICE + ap.getPatientId()).retrieve()
				.bodyToMono(PatientDto.class).block();

		if (patient == null) {
			throw new RuntimeException("Patient not found");
		}

		ap.setStatus("Confirmed");

		return repo.save(ap);
	}

	public List<Appointment> getAll() {
		return repo.findAll();
	}

	public List<AppointmentResponseDto> viewAll() {
		List<Appointment> appointments = repo.findAll();

		return appointments.stream().map(a -> {
			DoctorDto doctor = getDoctor(a.getDoctorId());

			PatientDto patient = getPatient(a.getPatientId());

			return new AppointmentResponseDto(a.getId(), doctor, patient, a.getAppointmentDate(),
					a.getAppointmentTime(), a.getReason(), a.getStatus());
		}).toList();
	}

	public Appointment getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

	public DoctorDto getDoctor(Long doctorId) {
//		return restTemplate.getForObject(DOCTOR_SERVICE + doctorId, DoctorDto.class);

		return webClientBuilder.build().get().uri("http://doctorService/doctor/" + doctorId).retrieve()
				.bodyToMono(DoctorDto.class).block();
	}

	public PatientDto getPatient(Long patientId) {
//		return restTemplate.getForObject(PATIENT_SERVICE + patientId, PatientDto.class);

		return webClientBuilder.build().get().uri("http://patientService/patient/" + patientId).retrieve()
				.bodyToMono(PatientDto.class).block();
	}

}
