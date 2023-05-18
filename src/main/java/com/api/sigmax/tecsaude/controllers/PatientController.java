package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.PatientProfileDto;
import com.api.sigmax.tecsaude.domain.dtos.PatientRegisterDto;
import com.api.sigmax.tecsaude.domain.mappers.PatientDtoMapper;
import com.api.sigmax.tecsaude.domain.model.Patient;
import com.api.sigmax.tecsaude.services.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v2/patient")
public class PatientController {

    private final PatientService patientService;
    private final PatientDtoMapper mapper;

    public PatientController(PatientService patientService, PatientDtoMapper mapper){
        this.patientService = patientService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<PatientProfileDto>> findAll(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(patientService.findAll()
                        .stream()
                        .map(mapper).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientProfileDto> findById(@PathVariable("id") UUID id){
        return patientService.findById(id).map(patient -> ResponseEntity.status(HttpStatus.OK).body(
                mapper.apply(patient)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<PatientProfileDto> register(@RequestBody PatientRegisterDto dto) {
        if(patientService.existsByCpf(dto.cpf()) || patientService.existsByRg(dto.rg())){
            throw new ResponseStatusException(HttpStatus.CONFLICT , "Cannot create two patients with the same cpf or rg");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.apply(patientService.save(new Patient(dto))));
    }

    @PutMapping
    public ResponseEntity<PatientProfileDto> update(@RequestBody Patient patient){
        if(!patientService.existsById(patient.getId())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(mapper.apply(patientService.update(patient)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        if(!patientService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            patientService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

}
