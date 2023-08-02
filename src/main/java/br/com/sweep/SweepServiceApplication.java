package br.com.sweep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SweepServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SweepServiceApplication.class, args);
	}

}
