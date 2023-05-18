package com.api.sigmax.tecsaude.domain.mappers;

import com.api.sigmax.tecsaude.domain.dtos.AppointmentDto;
import com.api.sigmax.tecsaude.domain.model.Appointment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AppointmentMapper implements Function<Appointment, AppointmentDto> {

    @Override
    public AppointmentDto apply(Appointment appointment) {
        return new AppointmentDto(appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getDescription(),
                appointment.getDiagnostic(),
                appointment.getTests(),
                appointment.getDate(),
                appointment.getStart(),
                appointment.getEndOfAppointment(),
                appointment.getCid(),
                appointment.getRestfulDays(),
                appointment.getStatus());
    }
}
