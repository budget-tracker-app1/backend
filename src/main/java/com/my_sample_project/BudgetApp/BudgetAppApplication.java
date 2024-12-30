package com.my_sample_project.BudgetApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.my_sample_project.BudgetApp")
@EnableJpaRepositories("com.my_sample_project.BudgetApp.repository")
public class BudgetAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(BudgetAppApplication.class, args);
	}
}
