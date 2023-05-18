package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentDto(UUID id, UUID patientId, UUID doctorId, String desc, String diagnostic,
                             String tests, LocalDate date, LocalTime start, LocalTime endOf, String cid,
                             int restfulDays, AppointmentStatus status) {
}
