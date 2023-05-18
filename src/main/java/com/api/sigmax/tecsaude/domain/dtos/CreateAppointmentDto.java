package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.model.Doctor;
import com.api.sigmax.tecsaude.domain.model.Patient;
import lombok.NonNull;

import java.util.UUID;

public record CreateAppointmentDto(@NonNull Patient patient, @NonNull Doctor doctor, @NonNull String date,
                                   @NonNull String start) {
}
