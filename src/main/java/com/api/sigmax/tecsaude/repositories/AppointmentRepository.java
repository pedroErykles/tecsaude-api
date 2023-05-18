package com.api.sigmax.tecsaude.repositories;

import com.api.sigmax.tecsaude.domain.enums.AppointmentStatus;
import com.api.sigmax.tecsaude.domain.model.Appointment;
import com.api.sigmax.tecsaude.domain.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    ArrayList<Appointment> findByPatientId(UUID patientId);
    ArrayList<Appointment> findByDoctorId(UUID doctorId);
    ArrayList<Appointment> findByStatus(AppointmentStatus appointmentStatus);
    ArrayList<Appointment> findByDate(LocalDate date);

    @Query(value = "SELECT COUNT(a) FROM Appointment a WHERE a.date=:date and a.start=:startTime and a.status !=CANCELED")
    int countByDateTime(@Param("date") LocalDate date, @Param("startTime")LocalTime time);

    @Query(value = "SELECT COUNT(a) FROM Appointment a JOIN a.patient p WHERE p.id=:patient_id and a.date=:date and a.status !=CANCELED")
    int countByPatientAndDate(@Param("patient_id") UUID patientId, @Param("date") LocalDate date);

    boolean existsById(UUID id);

}
