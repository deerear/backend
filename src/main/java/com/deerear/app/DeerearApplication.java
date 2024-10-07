package com.deerear.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = "com.deerear")
@EnableJpaAuditing
public class DeerearApplication {

	public static void main(String[] args) {

		// .env 파일 로드
		//Dotenv dotenv = Dotenv.load();
		SpringApplication.run(DeerearApplication.class, args);
	}
}