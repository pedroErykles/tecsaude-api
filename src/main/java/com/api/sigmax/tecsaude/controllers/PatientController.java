package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.PatientProfileDto;
import com.api.sigmax.tecsaude.domain.dtos.PatientRegisterDto;
import com.api.sigmax.tecsaude.domain.mappers.PatientDtoMapper;
import com.api.sigmax.tecsaude.domain.model.Patient;
import com.api.sigmax.tecsaude.responses.ErrorResponse;
import com.api.sigmax.tecsaude.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v2/patient")
public class PatientController {

    private final PatientService patientService;
    private final PatientDtoMapper mapper;

    public PatientController(PatientService patientService, PatientDtoMapper mapper){
        this.patientService = patientService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Rota que retorna todos os pacientes")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pacientes retornados com sucesso",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = PatientProfileDto.class
                                            )
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> findAll(){
        return new ResponseEntity<List<PatientProfileDto>>(patientService
                .findAll().stream().map(mapper).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rota que retorna um paciente pelo seu id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Paciente retornado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = PatientProfileDto.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Paciente não encontrado",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> findById(@PathVariable("id") UUID id){
        var user = patientService.findById(id);

        if(user.isEmpty()){
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(
                    "Paciente não encontrado",
                    HttpStatus.NOT_FOUND.value()
            ), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<PatientProfileDto>(mapper.apply(user.get()), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Rota para registrar um paciente"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Paciente registrado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = PatientProfileDto.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Formato de dados inválidos ou dados obrigatórios não foram enviados"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflito com dados de usuário únicos (cpf, rg)",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> register(@RequestBody PatientRegisterDto dto) {
        if(patientService.existsByCpf(dto.cpf()) || patientService.existsByRg(dto.rg())){
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(
                    "Paciente com o mesmo cpf ou rg já cadastrado",
                    HttpStatus.CONFLICT.value()
            ), HttpStatus.CONFLICT);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.apply(patientService.save(new Patient(dto))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rota para atualizar dados de um paciente")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dados atualizados com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = Patient.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Paciente não encontrado",
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
    public ResponseEntity<?> update(
            @PathVariable("id") UUID id, @RequestBody Patient patient){
        if(!patientService.existsById(id)){
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(
                    "",
                    HttpStatus.NOT_FOUND.value()
            ), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(mapper.apply(patientService.update(patient)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Rota para deletar dados de um paciente")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dados deletados com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = Patient.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Paciente não encontrado",
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
        if(!patientService.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            patientService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
