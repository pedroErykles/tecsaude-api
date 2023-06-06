package com.api.sigmax.tecsaude;

import com.api.sigmax.tecsaude.config.security.JwtService;
import com.api.sigmax.tecsaude.domain.enums.UserRole;
import com.api.sigmax.tecsaude.domain.model.User;
import com.api.sigmax.tecsaude.repositories.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Tec-Saude-API",
		description = "API para gerenciamento de consultas médicas",
		contact = @Contact(email = "pedroerykles@gmail.com", url = "github.com/pedroErykles"),
		version = "2.0"
))
@Slf4j
public class TecsaudeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TecsaudeApplication.class, args);
	}

	@Autowired
	private JwtService service;
	@Autowired
	private UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {
		var user = new User();
		user.setPassword(new BCryptPasswordEncoder().encode("1234"));
		user.setCpf("111.222.333-44");
		user.setRole(UserRole.ADMIN);
		user.setUsername("Ped");


		var token = service.generateToken(userRepository.save(user));
		log.info("token: {}", token);
	}
}
