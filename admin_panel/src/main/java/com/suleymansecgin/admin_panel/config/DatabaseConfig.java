package com.suleymansecgin.admin_panel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabaseConfig implements CommandLineRunner {
	
	@Value("${spring.jpa.properties.hibernate.default_schema:admin_panel}")
	private String schemaName;
	
	private final JdbcTemplate jdbcTemplate;
	
	public DatabaseConfig(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public void run(String... args) throws Exception {
		try {
			// Şema yoksa oluştur
			String checkSchemaSql = "SELECT EXISTS(SELECT 1 FROM information_schema.schemata WHERE schema_name = ?)";
			Boolean schemaExists = jdbcTemplate.queryForObject(checkSchemaSql, Boolean.class, schemaName);
			
			if (schemaExists == null || !schemaExists) {
				String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
				jdbcTemplate.execute(createSchemaSql);
				System.out.println("Schema '" + schemaName + "' created successfully.");
			}
		} catch (Exception e) {
			System.err.println("Error creating schema: " + e.getMessage());
			// Şema oluşturma hatası kritik değil, devam et
		}
	}
}

