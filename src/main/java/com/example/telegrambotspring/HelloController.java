package com.example.telegrambotspring;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@Bean
	public CommandLineRunner commandLineRunner(final ApplicationContext context) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beansNames = context.getBeanDefinitionNames();
			Arrays.sort(beansNames);
			for (String bean : beansNames) {
				System.out.println(bean);
			}
		};
	}
}
