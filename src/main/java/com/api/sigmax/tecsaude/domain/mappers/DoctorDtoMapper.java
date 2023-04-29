package com.api.sigmax.tecsaude.domain.mappers;


import com.api.sigmax.tecsaude.domain.dtos.DoctorProfileDto;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class DoctorDtoMapper implements Function<Doctor, DoctorProfileDto> {

    @Override
    public DoctorProfileDto apply(Doctor doctor) {
        return new DoctorProfileDto(
                doctor.getId(),
                doctor.getName(),
                doctor.getPhoneNumber(),
                doctor.getSpecialization(),
                doctor.getBirthday(),
                doctor.getGender()
        );
    }
}
