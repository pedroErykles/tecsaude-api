package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.enums.Gender;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;

public record PatientRegisterDto(@NonNull String name,
                                 @Pattern(regexp = "(^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$)\n") @NonNull String cpf,
                                 @Pattern(regexp = "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$") @NonNull String birthday,
                                 @Pattern(regexp = "(^[0-9]{2})?(\\s|-)?(9?[0-9]{4})-?([0-9]{4}$)") @NonNull String phoneNumber,
                                 @NonNull Gender gender,
                                 @Pattern(regexp = "(\\d{1,2}\\.?)(\\d{3}\\.?)(\\d{3})(\\-?[0-9Xx])") @NonNull String rg,
                                 String fatherName, String motherName) {
}
