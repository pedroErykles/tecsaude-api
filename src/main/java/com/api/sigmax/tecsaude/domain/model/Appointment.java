package com.api.sigmax.tecsaude.domain.model;

import com.api.sigmax.tecsaude.domain.dtos.CreateAppointmentDto;
import com.api.sigmax.tecsaude.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "TB_APPOINTMENT")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @OneToOne
    private Patient patient;
    @OneToOne
    private Doctor doctor;
    @Column(columnDefinition = "text")
    private String description;
    @Column(columnDefinition = "text")
    private String diagnostic;
    private String tests;
    private LocalDate date;
    private LocalTime start;
    private LocalTime endOfAppointment;
    private String cid;
    private int restfulDays;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    public Appointment(CreateAppointmentDto appointmentDto){
        this.patient = appointmentDto.patient();
        this.doctor = appointmentDto.doctor();
        this.date = LocalDate.parse(appointmentDto.date(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.start = LocalTime.parse(appointmentDto.start(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.status = AppointmentStatus.PENDING;
    }

}
