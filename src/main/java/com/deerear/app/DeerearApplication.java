package com.deerear.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.deerear")
public class DeerearApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeerearApplication.class, args);
	}
}