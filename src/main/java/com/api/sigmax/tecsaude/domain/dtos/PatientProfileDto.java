package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.enums.Gender;

import java.util.UUID;

public record PatientProfileDto(UUID id, String name, String phoneNumber, String birthday,
                                String cpf, Gender gender, String fatherName, String motherName) {
}
