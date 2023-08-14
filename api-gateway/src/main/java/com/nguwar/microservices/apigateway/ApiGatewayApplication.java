package com.nguwar.microservices.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {

		SpringApplication.run(ApiGatewayApplication.class, args);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {

			}
		};

		Runnable runnable1 = () -> {};

		new Thread(runnable).start();
	}

}
