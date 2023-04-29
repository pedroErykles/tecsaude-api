package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public record DoctorAvailableDto(@NonNull UUID id, @NonNull List<AvailableDay> availableDayList) {
}
