package com.api.sigmax.tecsaude.domain.dtos;

import jakarta.validation.constraints.Email;
import lombok.NonNull;

public record RegisterUserDto(@Email @NonNull String username, @NonNull String password, @NonNull String cpf) {
}
