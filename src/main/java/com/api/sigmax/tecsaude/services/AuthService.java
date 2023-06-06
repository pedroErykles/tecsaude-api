package com.api.sigmax.tecsaude.services;

import com.api.sigmax.tecsaude.config.security.JwtService;
import com.api.sigmax.tecsaude.domain.dtos.LoginDto;
import com.api.sigmax.tecsaude.domain.model.User;
import com.api.sigmax.tecsaude.repositories.UserRepository;
import com.api.sigmax.tecsaude.responses.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthResponse register(User user){
        //encoding password and saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        //return jwt token
        return new AuthResponse(
                jwtService.generateToken(user),
                user.getRole().name(),
                user.getCpf()
        );
    }

    public boolean find(String username){
        return userRepository.existsByUsername(username);
    }

    public AuthResponse authenticate(LoginDto dto){
        var user = userRepository.findByUsername(dto.username());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.username(), dto.password()
        ));

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getRole().name(),
                user.getCpf()
        );
    }

}
