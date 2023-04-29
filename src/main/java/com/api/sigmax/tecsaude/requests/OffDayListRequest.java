package com.api.sigmax.tecsaude.requests;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public record OffDayListRequest(@NonNull List<String> days, @NonNull UUID id) {
}
