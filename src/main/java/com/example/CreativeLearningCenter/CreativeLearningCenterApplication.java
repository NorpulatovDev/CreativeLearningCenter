package com.example.CreativeLearningCenter;

import com.example.CreativeLearningCenter.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class CreativeLearningCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreativeLearningCenterApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(AuthService authService) {
		return args -> {
			authService.createDefaultAdmin();
		};
	}
}