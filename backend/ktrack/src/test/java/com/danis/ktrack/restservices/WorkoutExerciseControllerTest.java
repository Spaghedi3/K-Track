package com.danis.ktrack.restservices;

import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import com.danis.ktrack.domain.model.enums.SetType;
import com.danis.ktrack.domain.model.valueobject.Weight;
import com.danis.ktrack.dto.workout.*;
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
public class WorkoutExerciseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Long existingWorkoutId;

    // Assumes an Exercise with ID 1 exists in your test database (data.sql)
    private final Long validExerciseId = 1L;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/workouts";

        // 1. Create a parent Workout to attach exercises to
        WorkoutCreateRequest createRequest = new WorkoutCreateRequest();
        createRequest.setUserId(5L); // Assumes User ID 5 exists
        createRequest.setName("Test Workout for Exercises");

        ResponseEntity<WorkoutResponse> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(baseUrl, createRequest, WorkoutResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        existingWorkoutId = createResponse.getBody().getId();
    }

    // --- Tests for WorkoutExercise ---

    @Test
    void testAddExerciseToWorkout() {
        // GIVEN
        WorkoutExerciseCreateDTO createDTO = new WorkoutExerciseCreateDTO();
        createDTO.setExerciseId(validExerciseId);
        createDTO.setOrderIndex(1);
        createDTO.setNotes("First set of squats");

        // WHEN
        ResponseEntity<WorkoutExerciseDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises",
                        createDTO,
                        WorkoutExerciseDTO.class
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getExerciseId()).isEqualTo(validExerciseId);
        assertThat(response.getBody().getOrderIndex()).isEqualTo(1);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void testUpdateExerciseInWorkout() {
        // 1. Add an exercise first
        WorkoutExerciseCreateDTO createDTO = new WorkoutExerciseCreateDTO();
        createDTO.setExerciseId(validExerciseId);
        createDTO.setOrderIndex(1);
        createDTO.setNotes("Initial notes");

        ResponseEntity<WorkoutExerciseDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises",
                        createDTO,
                        WorkoutExerciseDTO.class
                );
        Long workoutExerciseId = createResponse.getBody().getId();

        // 2. Prepare update data
        WorkoutExerciseCreateDTO updateDTO = new WorkoutExerciseCreateDTO();
        updateDTO.setExerciseId(validExerciseId);
        updateDTO.setOrderIndex(2);
        updateDTO.setNotes("Updated notes for second order");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WorkoutExerciseCreateDTO> entity = new HttpEntity<>(updateDTO, headers);

        // WHEN
        ResponseEntity<WorkoutExerciseDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId,
                        HttpMethod.PUT,
                        entity,
                        WorkoutExerciseDTO.class
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOrderIndex()).isEqualTo(2);
        assertThat(response.getBody().getNotes()).isEqualTo("Updated notes for second order");
        assertThat(response.getBody().getId()).isEqualTo(workoutExerciseId);
    }

    @Test
    void testGetWorkoutExercise() {
        // 1. Add an exercise
        WorkoutExerciseCreateDTO createDTO = new WorkoutExerciseCreateDTO();
        createDTO.setExerciseId(validExerciseId);
        createDTO.setOrderIndex(1);

        ResponseEntity<WorkoutExerciseDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises",
                        createDTO,
                        WorkoutExerciseDTO.class
                );
        Long workoutExerciseId = createResponse.getBody().getId();

        // WHEN
        ResponseEntity<WorkoutExerciseDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId,
                        WorkoutExerciseDTO.class
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(workoutExerciseId);
        assertThat(response.getBody().getExerciseId()).isEqualTo(validExerciseId);
    }

    @Test
    void testRemoveExerciseFromWorkout() {
        // 1. Add an exercise
        WorkoutExerciseCreateDTO createDTO = new WorkoutExerciseCreateDTO();
        createDTO.setExerciseId(validExerciseId);
        createDTO.setOrderIndex(1);

        ResponseEntity<WorkoutExerciseDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises",
                        createDTO,
                        WorkoutExerciseDTO.class
                );
        Long workoutExerciseId = createResponse.getBody().getId();

        // WHEN
        restTemplate
                .withBasicAuth("testuser", "testpass")
                .delete(baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId);

        // THEN - Verify deletion by attempting to get it again
        ResponseEntity<Void> verifyResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId,
                        Void.class
                );

        // Expecting 500 because the controller throws a RuntimeException when not found
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Tests for WorkoutSet ---

    // Helper to setup an exercise before adding sets
    private Long addExerciseAndGetId() {
        WorkoutExerciseCreateDTO createDTO = new WorkoutExerciseCreateDTO();
        createDTO.setExerciseId(validExerciseId);
        createDTO.setOrderIndex(1);
        ResponseEntity<WorkoutExerciseDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises",
                        createDTO,
                        WorkoutExerciseDTO.class
                );
        return createResponse.getBody().getId();
    }

    @Test
    void testAddSetToExercise() {
        Long workoutExerciseId = addExerciseAndGetId();

        // GIVEN
        WorkoutSetCreateDTO createDTO = new WorkoutSetCreateDTO();
        createDTO.setSetNumber(1);
        createDTO.setSetType(SetType.WARMUP);
        createDTO.setReps(10);
        // Correctly instantiate Weight value object
        createDTO.setWeight(new Weight(20.0, MeasurementUnit.KG));

        // WHEN
        ResponseEntity<WorkoutSetDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        createDTO,
                        WorkoutSetDTO.class
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSetNumber()).isEqualTo(1);
        assertThat(response.getBody().getWeight().getValue()).isEqualTo(20.0);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void testUpdateSet() {
        Long workoutExerciseId = addExerciseAndGetId();

        // 1. Create initial set
        WorkoutSetCreateDTO createDTO = new WorkoutSetCreateDTO();
        createDTO.setSetNumber(1);
        createDTO.setSetType(SetType.WARMUP);
        createDTO.setReps(10);
        createDTO.setWeight(new Weight(20.0, MeasurementUnit.KG));

        ResponseEntity<WorkoutSetDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        createDTO,
                        WorkoutSetDTO.class
                );
        Long setId = createResponse.getBody().getId();

        // 2. Update set
        WorkoutSetCreateDTO updateDTO = new WorkoutSetCreateDTO();
        updateDTO.setSetNumber(2);
        updateDTO.setSetType(SetType.NORMAL);
        updateDTO.setReps(8);
        updateDTO.setWeight(new Weight(60.0, MeasurementUnit.KG));
        updateDTO.setNotes("Heavy set");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WorkoutSetCreateDTO> entity = new HttpEntity<>(updateDTO, headers);

        // WHEN
        ResponseEntity<WorkoutSetDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets/" + setId,
                        HttpMethod.PUT,
                        entity,
                        WorkoutSetDTO.class
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSetNumber()).isEqualTo(2);
        assertThat(response.getBody().getWeight().getValue()).isEqualTo(60.0);
        assertThat(response.getBody().getNotes()).isEqualTo("Heavy set");
    }

    @Test
    void testDeleteSet() {
        Long workoutExerciseId = addExerciseAndGetId();

        // 1. Create set
        WorkoutSetCreateDTO createDTO = new WorkoutSetCreateDTO();
        createDTO.setSetNumber(1);
        createDTO.setSetType(SetType.NORMAL);
        createDTO.setWeight(new Weight(20.0, MeasurementUnit.KG));

        ResponseEntity<WorkoutSetDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        createDTO,
                        WorkoutSetDTO.class
                );
        Long setId = createResponse.getBody().getId();

        // WHEN
        restTemplate
                .withBasicAuth("testuser", "testpass")
                .delete(baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets/" + setId);

        // THEN - Verify deletion
        ResponseEntity<Void> verifyResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets/" + setId,
                        Void.class
                );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testCompleteSet() {
        Long workoutExerciseId = addExerciseAndGetId();

        // 1. Create set
        WorkoutSetCreateDTO createDTO = new WorkoutSetCreateDTO();
        createDTO.setSetNumber(1);
        createDTO.setSetType(SetType.NORMAL);
        createDTO.setWeight(new Weight(20.0, MeasurementUnit.KG));

        ResponseEntity<WorkoutSetDTO> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        createDTO,
                        WorkoutSetDTO.class
                );
        Long setId = createResponse.getBody().getId();

        // Verify initial state
        assertThat(createResponse.getBody().isCompleted()).isFalse();
        assertThat(createResponse.getBody().getCompletedAt()).isNull();

        // WHEN
        ResponseEntity<WorkoutSetDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets/" + setId + "/complete",
                        null,
                        WorkoutSetDTO.class
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isCompleted()).isTrue();
        assertThat(response.getBody().getCompletedAt()).isNotNull();
    }

    @Test
    void testGetExerciseSets() {
        Long workoutExerciseId = addExerciseAndGetId();

        // 1. Add two sets
        WorkoutSetCreateDTO set1 = new WorkoutSetCreateDTO();
        set1.setSetNumber(1);
        set1.setSetType(SetType.NORMAL);
        set1.setWeight(new Weight(20.0, MeasurementUnit.KG));
        restTemplate.withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        set1,
                        WorkoutSetDTO.class
                );

        WorkoutSetCreateDTO set2 = new WorkoutSetCreateDTO();
        set2.setSetNumber(2);
        set2.setSetType(SetType.NORMAL);
        set2.setWeight(new Weight(30.0, MeasurementUnit.KG));
        restTemplate.withBasicAuth("testuser", "testpass")
                .postForEntity(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        set2,
                        WorkoutSetDTO.class
                );

        // WHEN
        ResponseEntity<List<WorkoutSetDTO>> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange(
                        baseUrl + "/" + existingWorkoutId + "/exercises/" + workoutExerciseId + "/sets",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<WorkoutSetDTO>>() {}
                );

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getSetNumber()).isEqualTo(1);
        assertThat(response.getBody().get(1).getSetNumber()).isEqualTo(2);
    }
}