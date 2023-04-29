package com.api.sigmax.tecsaude;

import com.api.sigmax.tecsaude.domain.enums.Gender;
import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import com.api.sigmax.tecsaude.domain.model.Patient;
import com.api.sigmax.tecsaude.repositories.DoctorRepository;
import com.api.sigmax.tecsaude.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TecsaudeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TecsaudeApplication.class, args);
	}

	@Autowired
	DoctorRepository repository;

	@Override
	public void run(String... args) throws Exception {
		Doctor d = new Doctor();
		List<AvailableDay> days = new ArrayList<>();
		days.add(new AvailableDay(
				DayOfWeek.MONDAY,
				LocalTime.parse("14:30:00"),
				LocalTime.parse("16:00:00")
		));
		d.setName("Ped");
		d.setRg("");
		d.setCpf("");
		d.setBirthday("26/06/2006");
		d.setGender(Gender.MALE);
		d.setSpecialization("Pediatra");
		d.setPhoneNumber("");
		d.setAvailableDays(days);
		repository.save(d);

	}
}
