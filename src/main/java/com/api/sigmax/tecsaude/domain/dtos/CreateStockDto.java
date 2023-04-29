package com.api.sigmax.tecsaude.domain.dtos;

import lombok.NonNull;

public record CreateStockDto(@NonNull String name, @NonNull Integer quantity, @NonNull String itemType) {
}
