package com.api.sigmax.tecsaude.controllers;

import com.api.sigmax.tecsaude.domain.dtos.LoginDto;
import com.api.sigmax.tecsaude.domain.dtos.RegisterUserDto;
import com.api.sigmax.tecsaude.domain.model.User;
import com.api.sigmax.tecsaude.responses.AuthResponse;
import com.api.sigmax.tecsaude.responses.ErrorResponse;
import com.api.sigmax.tecsaude.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "signup route")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Conta criada com sucesso",
                    content = {
                            @Content(
                                    schema = @Schema(
                                            implementation = AuthResponse.class
                                    )
                            )
                    }
            )
    })
    public ResponseEntity<?> signUp(@RequestBody @Validated RegisterUserDto dto){
        return new ResponseEntity<AuthResponse>(authService.register(new User(dto)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login route")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login realizado com sucesso",
                            content = {
                                    @Content(
                                            schema = @Schema(
                                                    implementation = AuthResponse.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuário com essas credencias não encontrado",
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
    public ResponseEntity<?> login(@RequestBody @Validated LoginDto dto){
        if(!authService.find(dto.username())){
            var message = new ErrorResponse(
                    "Erro: Usuário não encontrado",
                    HttpStatus.NOT_FOUND.value()
            );

            return new ResponseEntity<ErrorResponse>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AuthResponse>(authService.authenticate(dto), HttpStatus.OK);
    }


}
