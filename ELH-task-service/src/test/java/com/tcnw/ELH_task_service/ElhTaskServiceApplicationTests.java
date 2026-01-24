package com.tcnw.ELH_task_service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
class ElhTaskServiceApplicationTests {

    @Mock
    private JwtDecoder jwtDecoder;
	@Test
	void contextLoads() {
	}

}
