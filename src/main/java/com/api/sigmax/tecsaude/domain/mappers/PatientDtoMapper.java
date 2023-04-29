package com.api.sigmax.tecsaude.domain.mappers;

import com.api.sigmax.tecsaude.domain.dtos.PatientProfileDto;
import com.api.sigmax.tecsaude.domain.model.Patient;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PatientDtoMapper implements Function<Patient, PatientProfileDto>{

    @Override
    public PatientProfileDto apply(Patient patient) {
        return new PatientProfileDto(
                patient.getId(),
                patient.getName(),
                patient.getPhoneNumber(),
                patient.getBirthday(),
                patient.getCpf(),
                patient.getGender(),
                patient.getFatherName(),
                patient.getMotherName()
                );
    }
}
