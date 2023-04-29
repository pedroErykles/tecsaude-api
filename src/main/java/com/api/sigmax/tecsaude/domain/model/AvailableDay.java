package com.api.sigmax.tecsaude.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "TB_AVAILABLE_DAY")
@NoArgsConstructor
@Data
public class AvailableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private DayOfWeek day;
    @Column(nullable = false)
    private LocalTime availableStart;
    @Column(nullable = false)
    private LocalTime availableEnding;

    public AvailableDay(DayOfWeek day, LocalTime availableStart, LocalTime availableEnding) {
        this.day = day;
        this.availableStart = availableStart;
        this.availableEnding = availableEnding;
    }




}
