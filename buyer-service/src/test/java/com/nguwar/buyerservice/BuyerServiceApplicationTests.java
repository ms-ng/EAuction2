package com.nguwar.buyerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.main.lazy-initialization=true",
		classes = {BuyerServiceApplication.class})
class BuyerServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
