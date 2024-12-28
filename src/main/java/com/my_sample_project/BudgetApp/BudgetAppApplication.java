package com.my_sample_project.BudgetApp;

import com.my_sample_project.BudgetApp.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.my_sample_project.BudgetApp")
@EnableJpaRepositories("com.my_sample_project.BudgetApp.repository")
public class BudgetAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(BudgetAppApplication.class, args);
	}

	// Load .env values into the Spring Environment
	public static class EnvLoader implements EnvironmentPostProcessor {
		@Override
		public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
			System.setProperty("DB_URL", EnvConfig.get("DB_URL"));
			System.setProperty("DB_USERNAME", EnvConfig.get("DB_USERNAME"));
			System.setProperty("DB_PASSWORD", EnvConfig.get("DB_PASSWORD"));
			System.setProperty("JWT_SECRET", EnvConfig.get("JWT_SECRET"));
		}
	}
}
