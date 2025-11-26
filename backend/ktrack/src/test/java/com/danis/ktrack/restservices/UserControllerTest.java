package com.danis.ktrack.restservices;

import com.danis.ktrack.dto.CreateUserRequest;
import com.danis.ktrack.dto.UpdateUserRequest;
import com.danis.ktrack.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.security.user.name=testuser",
                "spring.security.user.password=testpass"
        }
)
@Transactional
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
    }

    @Test
    void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");

        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("john_doe");
        assertThat(response.getBody().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testGetUserById() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("jane_doe");
        request.setEmail("jane@example.com");

        ResponseEntity<UserDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request, UserDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserDTO createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();

        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(baseUrl + "/" + createdUser.getId(), UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("jane_doe");
    }

    @Test
    void testGetUserByUsername() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("bob_smith");
        request.setEmail("bob@example.com");

        restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request, UserDTO.class);

        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(baseUrl + "/username/bob_smith", UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void testUpdateUser() {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setUsername("alice");
        createRequest.setEmail("alice@example.com");

        ResponseEntity<UserDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, createRequest, UserDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserDTO createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("alice_updated");
        updateRequest.setEmail("alice_updated@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange(
                        baseUrl + "/" + createdUser.getId(),
                        HttpMethod.PUT,
                        entity,
                        UserDTO.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("alice_updated");
        assertThat(response.getBody().getEmail()).isEqualTo("alice_updated@example.com");
    }

    @Test
    void testDeleteUser() {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setUsername("charlie");
        createRequest.setEmail("charlie@example.com");

        ResponseEntity<UserDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, createRequest, UserDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserDTO createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();

        restTemplate
                .withBasicAuth("testuser", "testpass")
                .delete(baseUrl + "/" + createdUser.getId());

        // Verify deletion
        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(baseUrl + "/" + createdUser.getId(), UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}