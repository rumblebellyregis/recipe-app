package com.project.recipeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class RecipeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeAppApplication.class, args);
	}

}
