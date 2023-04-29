package com.api.sigmax.tecsaude.domain.dtos;

import com.api.sigmax.tecsaude.domain.enums.Gender;

import java.util.UUID;

public record DoctorProfileDto(UUID id, String name, String phoneNumber, String specialization, String birthday, Gender gender) {
}
