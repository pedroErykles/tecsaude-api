package com.api.sigmax.tecsaude.repositories;

import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Page<Doctor> findBySpecialization(Pageable pageable, String specialization);
    boolean existsById(UUID id);
    @Query("SELECT d FROM Doctor d WHERE d.availableDays IN :availableDays")
    List<Doctor> findByAvailableDays(@Param("availableDays") List<AvailableDay> availableDays);

    @Query("SELECT d FROM Doctor d JOIN d.availableDays a WHERE a.day=:weekDay")
    ArrayList<Doctor> findByWeekDay(@Param("weekDay")DayOfWeek dayOfWeek);

    @Query("SELECT d FROM Doctor d JOIN d.dayOffList l WHERE l=:day")
    List<Doctor> findByOffDayList(@Param("day") LocalDate day);

}
