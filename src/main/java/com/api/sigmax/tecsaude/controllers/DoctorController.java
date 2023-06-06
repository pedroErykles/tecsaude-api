package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.DoctorAvailableDto;
import com.api.sigmax.tecsaude.domain.dtos.DoctorDaysListDto;
import com.api.sigmax.tecsaude.domain.dtos.DoctorProfileDto;
import com.api.sigmax.tecsaude.domain.mappers.DocAvailableMapper;
import com.api.sigmax.tecsaude.domain.mappers.DoctorDtoMapper;
import com.api.sigmax.tecsaude.domain.model.AvailableDay;
import com.api.sigmax.tecsaude.domain.model.Doctor;
import com.api.sigmax.tecsaude.requests.OffDayListRequest;
import com.api.sigmax.tecsaude.responses.ErrorResponse;
import com.api.sigmax.tecsaude.services.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v2/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorDtoMapper mapper;
    private final DocAvailableMapper availableMapper;

    public DoctorController(DoctorService doctorService, DoctorDtoMapper mapper, DocAvailableMapper availableMapper){
        this.doctorService = doctorService;
        this.mapper = mapper;
        this.availableMapper = availableMapper;
    }

    @GetMapping
    @Operation(summary = "Rota que retorna todos os médicos")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = {
                                    @Content(
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = DoctorController.class
                                                    )
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<List<Doctor>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(
                doctorService.findAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rota que retorna médicos por id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recurso retornado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = DoctorProfileDto.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = ErrorResponse.class
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> findById(@PathVariable("id") UUID id){
      if(!doctorService.existsById(id)){
          var message = new ErrorResponse(
                  "Médico não encontrado",
                  HttpStatus.NOT_FOUND.value()
          );
          return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
      }

      return ResponseEntity.status(HttpStatus.OK).body(mapper.apply(doctorService.findById(id).get()));
    }

    @GetMapping("/available")
    @Operation(summary = "Rota para listar médicos disponíveis por data e hora")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = {
                                    @Content(
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = DoctorProfileDto.class
                                                    )
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<List<DoctorProfileDto>> findByAvailableDays(@RequestBody List<AvailableDay> availableDays){
        List<Doctor> doctors = doctorService.findByAvailableDays(availableDays);

        return ResponseEntity.status(HttpStatus.OK)
                .body(doctors.stream().map(mapper).collect(Collectors.toList()));
    }

    @GetMapping("/available/day")
    @Operation(summary = "Rota que retorna médicos disponíveis em uma data específica")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = {
                                    @Content(
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = DoctorProfileDto.class
                                                    )
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<List<DoctorProfileDto>> findByDate(@RequestParam("day")
                                                                 @Pattern(regexp = "^([0-9]{2})-([0-9]{2})-([0-9]{4})$\n") String date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dateParsed = LocalDate.parse(date, formatter);

        //Doing a query in the database and getting the doctors that will be off on the date
        List<DoctorProfileDto> offDoctors = doctorService.findByOff(dateParsed).stream().map(mapper).toList();
        //Doing another query to get doctors that should be available on the date (day of week) and removing the offDoctors from it
        ArrayList<DoctorProfileDto> doctors = (ArrayList<DoctorProfileDto>) doctorService.findByWeekDay(dateParsed.getDayOfWeek())
                .stream().map(mapper).collect(Collectors.toList());
        doctors.removeAll(offDoctors);

        return ResponseEntity.status(HttpStatus.OK)
                .body(doctors);
    }

    @GetMapping("/available/all")
    @Operation(summary = "Rota que retorna os médicos e sua disponibilidade")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = {
                                    @Content(
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = DoctorDaysListDto.class
                                                    )
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<List<DoctorDaysListDto>> findAllAvailableDays(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(doctorService.findAll().stream().map(availableMapper).collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(summary = "Rota para registro de médicos")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Médico registrado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = Doctor.class
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<Doctor> save(@RequestBody Doctor doctor){
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.save(doctor));
    }

    @PutMapping
    @Operation(summary = "Rota para atualizar dados do médico")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Médico atualizado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = Doctor.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = ErrorResponse.class
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> update(@RequestBody Doctor doctor){
        if(doctorService.existsById(doctor.getId())){
            var message = new ErrorResponse(
                    "Médico não encontrado",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).body(doctorService.update(doctor));
    }

    @PutMapping("/available")
    @Operation(summary = "Rota para adicionar dias de disponibilidade para o médico")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recurso atualizado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = Doctor.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = ErrorResponse.class
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> addAvailableDays(@RequestBody DoctorAvailableDto dto){
        if(!doctorService.existsById(dto.id())){
            var message = new ErrorResponse(
                    "Médico não encontrado",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(doctorService.addAvailableDays(dto.id(), dto.availableDayList()));
    }

    @PutMapping("/offday/")
    @Operation(summary = "Rota para adicionar dia de folga para um médico")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Recurso atualizado com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = DoctorProfileDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> addDayOff(@RequestBody OffDayListRequest request){
        if(!doctorService.existsById(request.id())){
            var message  = new ErrorResponse(
                    "Médico não encontrado",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.apply(doctorService.addToOffDayList(request.days().stream().toList(), request.id())
                ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Rota para deletar um médico por id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Médico deletado com sucesso",
                            content =  {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = String.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recurso não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = ErrorResponse.class
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> delete(@PathVariable UUID id){
        if(doctorService.existsById(id)){
            var message = new ErrorResponse(
                    "Médico não encontrado",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }
            doctorService.delete(id);
            return new ResponseEntity<String>("Médico deletado com sucesso -> uid: " + id, HttpStatus.OK);
    }
}