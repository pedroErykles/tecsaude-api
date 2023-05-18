package com.api.sigmax.tecsaude.domain.model;

import com.api.sigmax.tecsaude.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "TB_DOCTOR")
@NoArgsConstructor
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<AvailableDay> availableDays;
    @Column(nullable = false)
    private String specialization;
    @Column(unique = true)
    private String cpf;
    @Column(unique = true)
    private String rg;
    private String phoneNumber;
    private String birthday;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ElementCollection
    private List<LocalDate> dayOffList;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Appointment> appointments;

}
