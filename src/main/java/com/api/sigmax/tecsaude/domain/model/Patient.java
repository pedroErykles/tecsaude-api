package com.api.sigmax.tecsaude.domain.model;

import com.api.sigmax.tecsaude.domain.dtos.PatientRegisterDto;
import com.api.sigmax.tecsaude.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "TB_PATIENT")
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    @Column(unique = true)
    private String cpf;
    @Column(unique = true)
    private String rg;
    private String birthday;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String fatherName;
    private String motherName;

    public Patient(PatientRegisterDto dto){
        this.name = dto.name();
        this.cpf = dto.cpf();
        this.rg = dto.rg();
        this.birthday = dto.birthday();
        this.gender = dto.gender();
        this.phoneNumber = dto.phoneNumber();
        this.fatherName = dto.fatherName();
        this.motherName = dto.motherName();
    }

}
