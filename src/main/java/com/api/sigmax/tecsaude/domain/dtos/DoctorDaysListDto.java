package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.model.AvailableDay;

import java.util.List;

public record DoctorDaysListDto(DoctorProfileDto dto, List<AvailableDay> days) {
}
