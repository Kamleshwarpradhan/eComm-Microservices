package com.app.ecomm.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerEcommApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerEcommApplication.class, args);
	}

}
