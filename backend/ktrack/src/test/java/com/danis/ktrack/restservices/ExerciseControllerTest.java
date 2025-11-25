package com.danis.ktrack.restservices;

import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.dto.exercise.ExerciseDTO;
import com.danis.ktrack.dto.exercise.ExerciseStatisticsRequest;
import com.danis.ktrack.dto.exercise.ExerciseSummaryRequest;
import com.danis.ktrack.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// 1. Start the full server on a random port
// 2. Define security props so we know the credentials to use
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.security.user.name=testuser",
        "spring.security.user.password=testpass"
})
class ExerciseControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private ExerciseService exerciseService;

    private ExerciseDTO exerciseDTO;
    private ExerciseSummaryRequest summaryDTO;
    private ExerciseStatisticsRequest statisticsDTO;

    @BeforeEach
    void setUp() {
        exerciseDTO = new ExerciseDTO();
        exerciseDTO.setId(1L);
        exerciseDTO.setName("Bench Press");
        exerciseDTO.setCategory(ExerciseCategory.BARBELL);
        exerciseDTO.setType(ExerciseType.STRENGTH);
        exerciseDTO.setPrimaryMuscleGroups(List.of(MuscleGroup.CHEST));
        exerciseDTO.setCustom(false);

        summaryDTO = new ExerciseSummaryRequest();
        summaryDTO.setId(1L);
        summaryDTO.setName("Bench Press");
        summaryDTO.setCategory(ExerciseCategory.BARBELL);
        summaryDTO.setPrimaryMuscleGroups(List.of(MuscleGroup.CHEST));

        statisticsDTO = new ExerciseStatisticsRequest();
        statisticsDTO.setExerciseId(1L);
        statisticsDTO.setTotalWorkouts(5);
        statisticsDTO.setTotalVolume(5000.0);
    }

    @Test
    void getAllExercises_ShouldReturnList() {
        // Arrange
        when(exerciseService.getAllExercises()).thenReturn(List.of(summaryDTO));

        // Act
        // We use ParameterizedTypeReference to handle List<Type> responses correctly
        ResponseEntity<List<ExerciseSummaryRequest>> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange(
                        "/api/v1/exercises",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {}
                );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Bench Press", response.getBody().get(0).getName());

        verify(exerciseService, times(1)).getAllExercises();
    }

    @Test
    void getExerciseById_ShouldReturnExercise() {
        // Arrange
        when(exerciseService.getExerciseById(1L)).thenReturn(exerciseDTO);

        // Act
        ResponseEntity<ExerciseDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity("/api/v1/exercises/1", ExerciseDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Bench Press", response.getBody().getName());
        assertEquals(ExerciseCategory.BARBELL, response.getBody().getCategory());

        verify(exerciseService, times(1)).getExerciseById(1L);
    }

    @Test
    void createExercise_ShouldReturnCreatedExercise() {
        // Arrange
        when(exerciseService.createExercise(any(ExerciseDTO.class))).thenReturn(exerciseDTO);

        // Act
        ResponseEntity<ExerciseDTO> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity("/api/v1/exercises", exerciseDTO, ExerciseDTO.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bench Press", response.getBody().getName());

        verify(exerciseService, times(1)).createExercise(any(ExerciseDTO.class));
    }

    @Test
    void deleteExercise_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(exerciseService).deleteExercise(1L);

        // Act
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange("/api/v1/exercises/1", HttpMethod.DELETE, null, Void.class);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(exerciseService, times(1)).deleteExercise(1L);
    }

    @Test
    void getExerciseStatistics_ShouldReturnStats() {
        // Arrange
        // Note: Controller expects "userId" parameter
        when(exerciseService.getExerciseStatistics(eq(1L), anyLong())).thenReturn(statisticsDTO);

        // Act
        ResponseEntity<ExerciseStatisticsRequest> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .getForEntity("/api/v1/exercises/1/statistics?userId=100", ExerciseStatisticsRequest.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getExerciseId());
        assertEquals(5, response.getBody().getTotalWorkouts());
        assertEquals(5000.0, response.getBody().getTotalVolume());

        verify(exerciseService, times(1)).getExerciseStatistics(eq(1L), anyLong());
    }

    @Test
    void unauthorizedAccess_ShouldFail() {
        // Act (No Auth provided)
        ResponseEntity<String> response = restTemplate
                .getForEntity("/api/v1/exercises", String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}