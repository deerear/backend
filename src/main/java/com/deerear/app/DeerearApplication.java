package com.deerear.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.deerear")
public class DeerearApplication {

	public static void main(String[] args) {

		// .env 파일 로드
		//Dotenv dotenv = Dotenv.load();
		SpringApplication.run(DeerearApplication.class, args);
	}
}