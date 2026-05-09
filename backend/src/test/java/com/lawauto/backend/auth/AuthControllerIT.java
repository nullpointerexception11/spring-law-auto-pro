package com.lawauto.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Arrange: Use the seed data credentials
        AuthController.LoginRequest request = new AuthController.LoginRequest("admin@prestige.com", "password");

        // Act
        ResponseEntity<AuthService.LoginResponse> response = restTemplate.postForEntity(
                "/api/auth/login", 
                request, 
                AuthService.LoginResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
        assertThat(response.getBody().role()).isNotBlank();
        assertThat(response.getBody().orgId()).isNotBlank();
    }
    
    @Test
    void shouldReturnErrorWithInvalidCredentials() {
        AuthController.LoginRequest request = new AuthController.LoginRequest("admin@prestige.com", "wrongpassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", 
                request, 
                String.class
        );

        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }
}
