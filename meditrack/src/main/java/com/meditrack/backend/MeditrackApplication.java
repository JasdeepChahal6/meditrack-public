package com.meditrack.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MeditrackApplication {


	public static void main(String[] args) {
		SpringApplication.run(MeditrackApplication.class, args);
	}

}
