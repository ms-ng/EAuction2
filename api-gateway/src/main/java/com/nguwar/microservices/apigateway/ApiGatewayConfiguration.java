package com.nguwar.microservices.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/get")
						.filters(f -> f
								.addRequestHeader("MyHeader", "MyURI")
								.addRequestParameter("Param", "MyValue"))
						.uri("http://httpbin.org:80"))
				.route(p -> p.path("/e-auction/api/v1/seller/**")
						.filters(f -> f.rewritePath(
								"/e-auction/api/v1/seller/(?<segment>.*)",
								"/e-auction/api/v1/seller/${segment}"))
						.uri("lb://seller-service"))
				.route(p -> p.path("/e-auction/api/v1/buyer/**")
						.filters(f -> f.rewritePath(
								"/e-auction/api/v1/buyer/(?<segment>.*)",
								"/e-auction/api/v1/buyer/${segment}"))
						.uri("lb://buyer-service"))
				.build();
	}

}
