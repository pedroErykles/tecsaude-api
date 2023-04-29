package com.api.sigmax.tecsaude.domain.mappers;

import com.api.sigmax.tecsaude.domain.dtos.DoctorAvailableDto;
import com.api.sigmax.tecsaude.domain.dtos.DoctorDaysListDto;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class DocAvailableMapper implements Function<Doctor, DoctorDaysListDto> {

    private DoctorDtoMapper mapper;

    public DocAvailableMapper(DoctorDtoMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public DoctorDaysListDto apply(Doctor doctor) {
        return new DoctorDaysListDto(mapper.apply(doctor), doctor.getAvailableDays());
    }
}
