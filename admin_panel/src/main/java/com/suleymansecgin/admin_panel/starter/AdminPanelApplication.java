package com.suleymansecgin.admin_panel.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"com.suleymansecgin"})
@EntityScan(basePackages = {"com.suleymansecgin"})
@EnableJpaRepositories(basePackages = {"com.suleymansecgin"})
@SpringBootApplication
public class AdminPanelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminPanelApplication.class, args);
	}

}
