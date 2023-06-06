package com.api.sigmax.tecsaude.domain.dtos;

import lombok.NonNull;

public record LoginDto(@NonNull String username, String password) {
}
