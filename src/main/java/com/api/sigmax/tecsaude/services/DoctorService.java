package com.api.sigmax.tecsaude.services;

import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import com.api.sigmax.tecsaude.repositories.DoctorRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DoctorService {

    private Logger log = LoggerFactory.getLogger(DoctorService.class);

    private DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    public Page<Doctor> pageAll(Pageable pageable){
        return doctorRepository.findAll(pageable);
    }

    public List<Doctor> findAll(){
        return doctorRepository.findAll();
    }

    public Page<Doctor> pageBySpecialization(Pageable pageable, String specialization){
        return doctorRepository.findBySpecialization(pageable, specialization);
    }

    public boolean existsById(UUID id){
        return doctorRepository.existsById(id);
    }

    public List<Doctor> findByAvailableDays(List<AvailableDay> days){
        return doctorRepository.findByAvailableDays(days);
    }

    public List<Doctor> findByOff(LocalDate day){
        return doctorRepository.findByOffDayList(day);
    }

    public Optional<Doctor> findById(UUID id){
        return doctorRepository.findById(id);
    }

    public ArrayList<Doctor> findByWeekDay(DayOfWeek dayOfWeek){
        return doctorRepository.findByWeekDay(dayOfWeek);
    }

    @Transactional
    public Doctor save(Doctor doctor){
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor update(Doctor doctor){
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor addAvailableDays(UUID id, List<AvailableDay> days){
        var doctor = findById(id).get();
        System.out.println(days.toString());
        doctor.getAvailableDays().addAll(days);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor addToOffDayList(List<String> daysString, UUID id){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<LocalDate> days = daysString.stream().map(
                day -> LocalDate.parse(day, formatter)
        ).toList();

        var doctor = doctorRepository.findById(id).get();
        doctor.getDayOffList().addAll(days);
        return doctorRepository.save(doctor);
    }

    public void delete(UUID id){
        doctorRepository.deleteById(id);
    }
}
