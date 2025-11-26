package com.danis.ktrack.restservices;

import com.danis.ktrack.dto.workout.WorkoutCreateRequest;
import com.danis.ktrack.dto.workout.WorkoutUpdateRequest;
import com.danis.ktrack.dto.workout.WorkoutResponse;
import com.danis.ktrack.dto.workout.WorkoutSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.security.user.name=testuser",
                "spring.security.user.password=testpass"
        }
)
@Transactional
public class WorkoutControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/workouts";
    }

    @Test
    void testStartWorkout() {
        WorkoutCreateRequest request = new WorkoutCreateRequest();
        request.setUserId(5L);
        request.setName("Morning Workout");

        ResponseEntity<WorkoutResponse> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request, WorkoutResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Morning Workout");
    }

    @Test
    void testGetWorkoutById() {
        WorkoutCreateRequest request = new WorkoutCreateRequest();
        request.setUserId(5L);
        request.setName("Evening Workout");

        ResponseEntity<WorkoutResponse> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request, WorkoutResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        WorkoutResponse createdWorkout = createResponse.getBody();
        assertThat(createdWorkout).isNotNull();

        ResponseEntity<WorkoutResponse> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(baseUrl + "/" + createdWorkout.getId(), WorkoutResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Evening Workout");
    }

//    @Test
//    void testUpdateWorkout() {
//        WorkoutCreateRequest createRequest = new WorkoutCreateRequest();
//        createRequest.setUserId(5L);
//        createRequest.setName("Leg Day");
//
//        ResponseEntity<WorkoutResponse> createResponse = restTemplate
//                .withBasicAuth("testuser", "testpass")
//                .postForEntity(baseUrl, createRequest, WorkoutResponse.class);
//
//        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        WorkoutResponse createdWorkout = createResponse.getBody();
//        assertThat(createdWorkout).isNotNull();
//
//        WorkoutUpdateRequest updateRequest = new WorkoutUpdateRequest();
//        updateRequest.setName("Leg Day Updated");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<WorkoutUpdateRequest> entity = new HttpEntity<>(updateRequest, headers);
//
//        ResponseEntity<WorkoutResponse> response = restTemplate
//                .withBasicAuth("testuser", "testpass")
//                .exchange(
//                        baseUrl + "/" + createdWorkout.getId(),
//                        HttpMethod.PUT,
//                        entity,
//                        WorkoutResponse.class
//                );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getName()).isEqualTo("Leg Day Updated");
//    }

    @Test
    void testDeleteWorkout() {
        WorkoutCreateRequest createRequest = new WorkoutCreateRequest();
        createRequest.setUserId(5L);
        createRequest.setName("Cardio Session");

        ResponseEntity<WorkoutResponse> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, createRequest, WorkoutResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        WorkoutResponse createdWorkout = createResponse.getBody();
        assertThat(createdWorkout).isNotNull();

        restTemplate
                .withBasicAuth("testuser", "testpass")
                .delete(baseUrl + "/" + createdWorkout.getId());


        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(baseUrl + "/" + createdWorkout.getId(), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testCompleteWorkout() {
        WorkoutCreateRequest createRequest = new WorkoutCreateRequest();
        createRequest.setUserId(5L);
        createRequest.setName("Full Body Workout");

        ResponseEntity<WorkoutResponse> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, createRequest, WorkoutResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        WorkoutResponse createdWorkout = createResponse.getBody();
        assertThat(createdWorkout).isNotNull();

        ResponseEntity<WorkoutResponse> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + createdWorkout.getId() + "/complete",
                        null,
                        WorkoutResponse.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus().toString()).isEqualTo("COMPLETED");;
    }

    @Test
    void testGetUserWorkouts() {
        Long userId = 5L;

        // Create first workout
        WorkoutCreateRequest request1 = new WorkoutCreateRequest();
        request1.setUserId(userId);
        request1.setName("Workout 1");

        restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request1, WorkoutResponse.class);

        // Create second workout
        WorkoutCreateRequest request2 = new WorkoutCreateRequest();
        request2.setUserId(userId);
        request2.setName("Workout 2");

        restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, request2, WorkoutResponse.class);

        ResponseEntity<List<WorkoutResponse>> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange(
                        baseUrl + "/user/" + userId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<WorkoutResponse>>() {}
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetWorkoutSummary() {
        WorkoutCreateRequest createRequest = new WorkoutCreateRequest();
        createRequest.setUserId(5L);
        createRequest.setName("Summary Test Workout");

        ResponseEntity<WorkoutResponse> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, createRequest, WorkoutResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        WorkoutResponse createdWorkout = createResponse.getBody();
        assertThat(createdWorkout).isNotNull();

        ResponseEntity<WorkoutSummaryResponse> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(
                        baseUrl + "/" + createdWorkout.getId() + "/summary",
                        WorkoutSummaryResponse.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}