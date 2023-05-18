package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.AppointmentDto;
import com.api.sigmax.tecsaude.domain.dtos.CreateAppointmentDto;
import com.api.sigmax.tecsaude.domain.enums.AppointmentStatus;
import com.api.sigmax.tecsaude.domain.mappers.AppointmentMapper;
import com.api.sigmax.tecsaude.domain.model.Appointment;
import com.api.sigmax.tecsaude.services.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/appointment")
public class AppointmentController {

    private Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;
    private final AppointmentMapper mapper;

    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentMapper mapper){
        this.appointmentService = appointmentService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> findAppointments(){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findAll().stream().map(mapper).toList());
    }

    @GetMapping("/status")
    public ResponseEntity<List<AppointmentDto>> findByStatus(@RequestParam("type")AppointmentStatus appointmentStatus){
        return ResponseEntity.status(HttpStatus.OK)
                .body(appointmentService.findByStatus(appointmentStatus).stream().map(mapper).toList());
    }

    @GetMapping("patient/{id}")
    public ResponseEntity<List<AppointmentDto>> findByPatientId(@PathVariable("id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findByPatientId(id)
                .stream().map(mapper).toList());
    }

    @GetMapping("doctor/{id}")
    public ResponseEntity<List<AppointmentDto>> findByDoctorId(@PathVariable("id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findByDoctorId(id)
                .stream().map(mapper).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> findById(@PathVariable UUID id){
        var appointment =  appointmentService.findById(id);

        return appointment.map(value -> ResponseEntity.status(HttpStatus.OK)
                .body(value)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("date")
    public ResponseEntity<List<AppointmentDto>> findByDate(@RequestParam("d")String date){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findByDate(
           LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .stream().map(mapper).toList());
    }

    @PostMapping
    public ResponseEntity<Appointment> save(@RequestBody CreateAppointmentDto dto){
        var appointment = new Appointment(dto);
        var date = appointment.getDate();

        //Verify if the appointment date has at least one day interval from the current date
        boolean oneDayInterval = date.isEqual(LocalDate.now().plus(1, ChronoUnit.DAYS)) ||
                date.isAfter(LocalDate.now().plus(1, ChronoUnit.DAYS));
        //Verify if the appointment date is in the doctor availability
        boolean verifySchedule = appointmentService.verifyScheduledTime(dto.doctor().getAvailableDays(),
                appointment.getStart(), appointment.getDate());

        log.info("oneDayInterval {}", oneDayInterval);
        log.info("verifySchedule {}", verifySchedule);

        if(!oneDayInterval || !verifySchedule){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(appointmentService.isThereByDateTime(appointment.getDate(), appointment.getStart()) ||
                appointmentService.sameDayAppointment(dto.patient().getId(), date)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.save(appointment));
    }

    @PutMapping
    public ResponseEntity<Appointment> update(@RequestBody Appointment appointment) {
        if (!appointmentService.existsById(appointment.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        appointment.setEndOfAppointment(LocalTime.parse(DateTimeFormatter.ofPattern("hh:mm:ss").format(LocalTime.now())));
        appointment.setStatus(AppointmentStatus.DONE);

        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.update(appointment));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<AppointmentDto> cancel(@PathVariable("id") UUID id){
        var optional = appointmentService.findById(id);

        if(optional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Appointment appointment = optional.get();
        appointment.setStatus(AppointmentStatus.CANCELED);

        return ResponseEntity.status(HttpStatus.OK).body(mapper.apply(appointmentService.update(appointment)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        if(!appointmentService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else{
            appointmentService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
