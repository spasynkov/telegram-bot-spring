package com.example.telegrambotspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AccessingDataMongodbApplication implements CommandLineRunner {
	@Autowired
	private CustomerRepository repository;

	@Override
	public void run(String... args) throws Exception {
		repository.deleteAll();

		repository.save(new Customer("Vasya", "Pupkin"));
		repository.save(new Customer("Kolya", "Shutkin"));
		repository.save(new Customer("Petya", "Shutkin"));

		System.out.println("printing users");
		repository.findAll().forEach(System.out::println);

		System.out.println("\nprinting user Vasya");
		System.out.println(repository.findByFirstName("Vasya"));

		System.out.println("\nprinting users Shutkin");
		repository.findByLastName("Shutkin").forEach(System.out::println);
	}
}
