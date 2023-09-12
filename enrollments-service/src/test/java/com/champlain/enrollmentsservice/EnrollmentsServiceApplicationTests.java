package com.champlain.enrollmentsservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootTest
class EnrollmentsServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testMainMethod() {
		EnrollmentsServiceApplication.main(new String[] {

		});
	}
}
