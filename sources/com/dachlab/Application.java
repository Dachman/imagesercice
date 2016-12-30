package com.dachlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.dachlab" })
@EntityScan(basePackages = { "com.dachlab" })
@EnableJpaRepositories(basePackages = "com.dachlab")
@Configuration
@PropertySource(value = {"imageservice-${spring.profiles.active:default}.properties", "application-${spring.profiles.active:default}.properties"} )
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
