package com.api.sigmax.tecsaude.services;

import com.api.sigmax.tecsaude.domain.enums.AppointmentStatus;
import com.api.sigmax.tecsaude.domain.model.Appointment;
import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import com.api.sigmax.tecsaude.repositories.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository){
        this.appointmentRepository =  appointmentRepository;
    }

    public List<Appointment> findAll(){
        return appointmentRepository.findAll();
    }

    public ArrayList<Appointment> findByPatientId(UUID id){
        return appointmentRepository.findByPatientId(id);
    }

    public ArrayList<Appointment> findByDoctorId(UUID id){
        return appointmentRepository.findByDoctorId(id);
    }

    public ArrayList<Appointment> findByStatus(AppointmentStatus appointmentStatus){
        return appointmentRepository.findByStatus(appointmentStatus);
    }

    public ArrayList<Appointment> findByDate(LocalDate date){
        return appointmentRepository.findByDate(date);
    }

    public ArrayList<Appointment> findByPatientCpf(String cpf){
        return appointmentRepository.findByPatientCpf(cpf);
    }

    public ArrayList<Appointment> findByDoctorCpf(String cpf){
        return appointmentRepository.findByDoctorCpf(cpf);
    }

    public Optional<Appointment> findById(UUID id){
        return appointmentRepository.findById(id);
    }

    public boolean existsById(UUID id){
        return appointmentRepository.existsById(id);
    }


    @Transactional
    public Appointment save(Appointment appointment){
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment update(Appointment appointment){
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void delete(UUID id){
        appointmentRepository.deleteById(id);
    }


    public boolean verifyScheduledTime(List<AvailableDay> availableDays, LocalTime time, LocalDate date){
        List<AvailableDay> days = availableDays.stream()
                .filter(availableDay -> time.isBefore(availableDay.getAvailableEnding()) &&
                        time.isAfter(availableDay.getAvailableStart()) &&
                        date.getDayOfWeek().equals(availableDay.getDay()))
                .toList();

        return days.size() > 0;
    }

    public boolean isThereByDateTime(LocalDate date, LocalTime startTime){
        return appointmentRepository.countByDateTime(date, startTime) > 0;
    }

    public boolean sameDayAppointment(UUID id, LocalDate date){
        return appointmentRepository.countByPatientAndDate(id, date) > 0;
    }
}
