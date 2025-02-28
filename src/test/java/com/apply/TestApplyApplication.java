package com.apply;

import org.springframework.boot.SpringApplication;

public class TestApplyApplication {

	public static void main(String[] args) {
		SpringApplication.from(ApplyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
