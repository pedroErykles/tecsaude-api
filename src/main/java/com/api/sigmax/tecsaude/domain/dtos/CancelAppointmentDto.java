package com.api.sigmax.tecsaude.domain.dtos;

import lombok.NonNull;

import java.util.UUID;

public record CancelAppointmentDto(@NonNull UUID patientId, @NonNull UUID appointmentId) {
}
