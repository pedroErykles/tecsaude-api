package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.DoctorAvailableDto;
import com.api.sigmax.tecsaude.domain.dtos.DoctorDaysListDto;
import com.api.sigmax.tecsaude.domain.dtos.DoctorProfileDto;
import com.api.sigmax.tecsaude.domain.mappers.DocAvailableMapper;
import com.api.sigmax.tecsaude.domain.mappers.DoctorDtoMapper;
import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import com.api.sigmax.tecsaude.requests.OffDayListRequest;
import com.api.sigmax.tecsaude.services.DoctorService;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v2/doctor")
public class DoctorController {

    private DoctorService doctorService;
    private DoctorDtoMapper mapper;
    private DocAvailableMapper availableMapper;

    public DoctorController(DoctorService doctorService, DoctorDtoMapper mapper, DocAvailableMapper availableMapper){
        this.doctorService = doctorService;
        this.mapper = mapper;
        this.availableMapper = availableMapper;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(
                doctorService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileDto> findById(@PathVariable("id") UUID id){
      if(!doctorService.existsById(id)){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      return ResponseEntity.status(HttpStatus.OK).body(mapper.apply(doctorService.findById(id).get()));
    }

    @GetMapping("/available")
    public ResponseEntity<List<DoctorProfileDto>> findByAvailableDays(@RequestBody List<AvailableDay> availableDays){
        List<Doctor> doctors = doctorService.findByAvailableDays(availableDays);

        if(doctors.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(doctors.stream().map(doctor -> mapper.apply(doctor)).collect(Collectors.toList()));
    }

    @GetMapping("/available/day")
    public ResponseEntity<List<DoctorProfileDto>> findByDate(@RequestParam("day")
                                                                 @Pattern(regexp = "^([0-9]{2})-([0-9]{2})-([0-9]{4})$\n") String date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dateParsed = LocalDate.parse(date, formatter);

        //Doing a query in the database and getting the doctors that will be off on the date
        List<DoctorProfileDto> offDoctors = doctorService.findByOff(dateParsed).stream().map(mapper).toList();
        //Doing another query to get doctors that should be available on the date (day of week) and removing the offDoctors from it
        ArrayList<DoctorProfileDto> doctors = (ArrayList<DoctorProfileDto>) doctorService.findByWeekDay(dateParsed.getDayOfWeek())
                .stream().map(doctor -> mapper.apply(doctor)).collect(Collectors.toList());
        doctors.removeAll(offDoctors);

        return ResponseEntity.status(HttpStatus.OK)
                .body(doctors);
    }

    @GetMapping("/available/all")
    public ResponseEntity<List<DoctorDaysListDto>> findAllAvailableDays(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(doctorService.findAll().stream().map(doctor -> availableMapper.apply(doctor)).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<Doctor> save(@RequestBody Doctor doctor){
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.save(doctor));
    }

    @PutMapping
    public ResponseEntity<Doctor> update(@RequestBody Doctor doctor){
        if(doctorService.existsById(doctor.getId())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(doctorService.update(doctor));
    }

    @PutMapping("/available")
    public ResponseEntity<Doctor> addAvailableDays(@RequestBody DoctorAvailableDto dto){
        if(!doctorService.existsById(dto.id())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(doctorService.addAvailableDays(dto.id(), dto.availableDayList()));
    }

    @PutMapping("/offday/")
    public ResponseEntity<DoctorProfileDto> addDayOff(@RequestBody OffDayListRequest request){
        if(!doctorService.existsById(request.id())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.apply(doctorService.addToOffDayList(request.days().stream().toList(), request.id())
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        if(doctorService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
            doctorService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
    }
}