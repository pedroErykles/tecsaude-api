package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.AppointmentDto;
import com.api.sigmax.tecsaude.domain.dtos.CreateAppointmentDto;
import com.api.sigmax.tecsaude.domain.enums.AppointmentStatus;
import com.api.sigmax.tecsaude.domain.mappers.AppointmentMapper;
import com.api.sigmax.tecsaude.domain.model.Appointment;
import com.api.sigmax.tecsaude.responses.ErrorResponse;
import com.api.sigmax.tecsaude.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper mapper;

    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentMapper mapper){
        this.appointmentService = appointmentService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "FindAllAppoinments Route")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Listed all appointments",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class)))
                    }
            )
    )
    public ResponseEntity<?> findAppointments(){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findAll().stream().map(mapper).toList());
    }

    @GetMapping("/status")
    @Operation(summary = "FindAllAppoinments Route")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Listed all appointments by status",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class)))
                    }
            )
    )
    public ResponseEntity<List<AppointmentDto>> findByStatus(@RequestParam("type")AppointmentStatus appointmentStatus){
        return ResponseEntity.status(HttpStatus.OK)
                .body(appointmentService.findByStatus(appointmentStatus).stream().map(mapper).toList());
    }

    @GetMapping("patient/byCpf")
    @Operation(summary = "Rota que retorna consultas pelo cpf do paciente")
    @ApiResponses(
       value = {
               @ApiResponse(
                       responseCode = "200",
                       description = "Consulta retornada com sucesso",
                       content = {
                               @Content(array = @ArraySchema(
                                       schema = @Schema(implementation = AppointmentDto.class)
                               ))
                       }
               )
       }
    )
    public ResponseEntity<?> findByPatientCpf(@RequestBody String cpf){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findByPatientCpf(cpf)
                .stream().map(mapper).toList());
    }

    @GetMapping("doctor/byCpf")
    @Operation(summary = "Rota que retorna consultas pelo cpf do médico")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Consulta retornada com sucesso",
                            content = {
                                    @Content(array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = AppointmentDto.class
                                            )
                                    ))
                            }
                    )
            }

    )
    public ResponseEntity<?> findByDoctorCpf(@RequestBody String cpf){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findByDoctorCpf(cpf)
                .stream().map(mapper).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rota responsável por encontrar a consulta por seu ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta retornada com sucesso",
                    content = {
                            @Content(schema = @Schema(implementation = Appointment.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
            })
    public ResponseEntity<?> findById(@PathVariable UUID id){
        var appointment =  appointmentService.findById(id);

        if(appointment.isEmpty()){
            var message = new ErrorResponse(
                    "Consulta não existe",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Appointment>(appointment.get(), HttpStatus.OK);
    }

    @GetMapping("date")
    @Operation(summary = "Rota que retorna consultas pelas datas")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = Appointment.class)))
                    }
            )
    })
    public ResponseEntity<?> findByDate(@RequestParam("d")String date){
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.findByDate(
           LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .stream().map(mapper).toList());
    }

    @PostMapping
    @Operation(summary = "Rota que marca uma consulta")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Consulta marcada com sucesso",
                    content = {
                            @Content(schema = @Schema(implementation = Appointment.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data da consulta inválida ou dados da consulta inválidos",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito com outra consulta",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
    })
    public ResponseEntity<?> save(@RequestBody CreateAppointmentDto dto){
        var appointment = new Appointment(dto);
        var date = appointment.getDate();

        //Verify if the appointment date has at least one day interval from the current date
        boolean oneDayInterval = date.isEqual(LocalDate.now().plus(1, ChronoUnit.DAYS)) ||
                date.isAfter(LocalDate.now().plus(1, ChronoUnit.DAYS));
        //Verify if the appointment date is in the doctor availability
        boolean verifySchedule = appointmentService.verifyScheduledTime(dto.doctor().getAvailableDays(),
                appointment.getStart(), appointment.getDate());

        if(!oneDayInterval || !verifySchedule){
            var message  = new ErrorResponse(
                    "Data inválida, verifique se os dados estão corretos e o há o intervalo de um dia",
                    HttpStatus.BAD_REQUEST.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.BAD_REQUEST);
        }

        if(appointmentService.isThereByDateTime(appointment.getDate(), appointment.getStart()) ||
                appointmentService.sameDayAppointment(dto.patient().getId(), date)){
            var message  = new ErrorResponse(
                    "Horário já agendado",
                    HttpStatus.CONFLICT.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.CONFLICT);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.save(appointment));
    }

    @PutMapping
    @Operation(summary = "Rota para atualização de consultas")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Consulta atualizada com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = Appointment.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> update(@RequestBody Appointment appointment) {
        if (!appointmentService.existsById(appointment.getId())) {
            var message = new ErrorResponse(
                    "Consulta não existe",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }

        appointment.setEndOfAppointment(LocalTime.parse(DateTimeFormatter.ofPattern("hh:mm:ss").format(LocalTime.now())));

        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.update(appointment));
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "Rota para cancelamento de consultas")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Consulta cancelada com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = Appointment.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> cancel(@PathVariable("id") UUID id){
        var optional = appointmentService.findById(id);

        if(optional.isEmpty()){
            var message = new ErrorResponse(
                    "Consulta não existe",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }

        Appointment appointment = optional.get();
        appointment.setStatus(AppointmentStatus.CANCELED);

        return ResponseEntity.status(HttpStatus.OK).body(mapper.apply(appointmentService.update(appointment)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Rota para deletar consultas")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Consulta deletada com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = String.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> delete(@PathVariable UUID id){
        if(!appointmentService.existsById(id)){
            var message = new ErrorResponse(
                    "Consulta não existe",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        } else{
            appointmentService.delete(id);
            return new ResponseEntity<String>("Consulta deletada com sucesso / uid: " + id, HttpStatus.OK);
        }
    }
}
