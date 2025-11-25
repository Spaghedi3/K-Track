package com.danis.ktrack.restservices;

import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.dto.exercise.ExerciseDTO;
import com.danis.ktrack.dto.exercise.ExerciseStatisticsRequest;
import com.danis.ktrack.dto.exercise.ExerciseSummaryRequest;
import com.danis.ktrack.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // NEW IMPORT
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ExerciseController.class)
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExerciseService exerciseService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExerciseDTO exerciseDTO;
    private ExerciseSummaryRequest summaryDTO;
    private ExerciseStatisticsRequest statisticsDTO;

    @BeforeEach
    void setUp() {
        // Setup dummy data
        exerciseDTO = new ExerciseDTO();
        exerciseDTO.setId(1L);
        exerciseDTO.setName("Bench Press");
        exerciseDTO.setDescription("Chest exercise");
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
    @WithMockUser(username = "user")
    void getAllExercises_ShouldReturnList() throws Exception {
        when(exerciseService.getAllExercises()).thenReturn(List.of(summaryDTO));

        mockMvc.perform(get("/api/v1/exercises"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Bench Press"));

        verify(exerciseService, times(1)).getAllExercises();
    }

    @Test
    @WithMockUser(username = "user")
    void getExerciseById_ShouldReturnExercise() throws Exception {
        // 1. Setup the specific data we expect for THIS test
        ExerciseDTO mockResponse = new ExerciseDTO();
        mockResponse.setId(1L);
        mockResponse.setName("Bench Press"); // matches jsonPath value below
        mockResponse.setCategory(ExerciseCategory.BARBELL); // matches jsonPath value below
        mockResponse.setType(ExerciseType.STRENGTH);
        mockResponse.setCustom(false);

        // 2. Tell the Mock Service: "When controller asks for ID 1, return this object"
        when(exerciseService.getExerciseById(eq(1L))).thenReturn(mockResponse);

        // 3. Perform the request and verify
        mockMvc.perform(get("/api/v1/exercises/1"))
                .andDo(print()) // This prints the JSON to the console for debugging
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bench Press"))
                .andExpect(jsonPath("$.category").value("BARBELL"));

        verify(exerciseService, times(1)).getExerciseById(eq(1L));
    }

    @Test
    @WithMockUser(username = "user")
    void createExercise_ShouldReturnCreatedExercise() throws Exception {
        when(exerciseService.createExercise(any(ExerciseDTO.class))).thenReturn(exerciseDTO);

        mockMvc.perform(post("/api/v1/exercises")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bench Press"));

        verify(exerciseService, times(1)).createExercise(any(ExerciseDTO.class));
    }

    @Test
    @WithMockUser(username = "user")
    void deleteExercise_ShouldReturnNoContent() throws Exception {
        doNothing().when(exerciseService).deleteExercise(1L);

        mockMvc.perform(delete("/api/v1/exercises/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(exerciseService, times(1)).deleteExercise(1L);
    }

    @Test
    @WithMockUser(username = "user")
    void getExerciseStatistics_ShouldReturnStats() throws Exception {
        when(exerciseService.getExerciseStatistics(eq(1L), anyLong())).thenReturn(statisticsDTO);

        mockMvc.perform(get("/api/v1/exercises/1/statistics")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseId").value(1))
                .andExpect(jsonPath("$.totalWorkouts").value(5));
    }
}