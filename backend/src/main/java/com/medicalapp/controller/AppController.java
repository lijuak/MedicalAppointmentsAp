package com.medicalapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
	
	@GetMapping(value = "")
	public String sayHello() {
		return "Welcome to Medical Appointments API";
	}
}
