package com.aurum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.aurum")
public class AurumApplication {

	public static void main(String[] args) {
		SpringApplication.run(AurumApplication.class, args);
	}
	
	@Bean
	  CommandLineRunner logSecurityFilterChains(ApplicationContext context) {
	    return args -> {
	      Map<String, SecurityFilterChain> beans =
	          context.getBeansOfType(SecurityFilterChain.class);

	      System.out.println("=== SecurityFilterChain beans ===");
	      beans.forEach((name, bean) ->
	          System.out.println("Bean name: " + name + " -> " + bean.getClass())
	      );
	      System.out.println("================================");
	    };
	  }

}
