package com.apply;

import com.apply.serviceImpl.AutomationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplyApplication {

	@Autowired
	private AutomationService automationService;

	public static void main(String[] args) {
		SpringApplication.run(ApplyApplication.class, args);
	}

	@PostConstruct
	public void init() {
		try {
			automationService.applyForAllUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
