package com.danis.ktrack.dto.exercise;

import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.domain.model.valueobject.ExerciseMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseMapperTest {

    private ExerciseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExerciseMapper();
    }

    @Test
    void toDTO_ShouldMapEntityFieldsToDTO() {
        // Arrange
        User creator = new User();
        creator.setId(10L);
        creator.setUsername("creator");

        ExerciseMetadata metadata = new ExerciseMetadata();
        metadata.setName("Squat");
        metadata.setDescription("Legs");
        metadata.setInstructions("Down and up");
        metadata.setImageUrl("img.jpg");
        metadata.setVideoUrl("vid.mp4");

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setMetadata(metadata);
        exercise.setPrimaryMuscleGroups(List.of(MuscleGroup.QUADRICEPS));
        exercise.setSecondaryMuscleGroups(List.of(MuscleGroup.GLUTES));
        exercise.setCategory(ExerciseCategory.BARBELL);
        exercise.setType(ExerciseType.STRENGTH);
        exercise.setCustom(true);
        exercise.setCreatedByUser(creator);

        // Act
        ExerciseDTO result = mapper.toDTO(exercise);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Squat", result.getName());
        assertEquals("Legs", result.getDescription());
        assertEquals("Down and up", result.getInstructions());
        assertEquals("img.jpg", result.getImageUrl());
        assertEquals("vid.mp4", result.getVideoUrl());
        assertEquals(1, result.getPrimaryMuscleGroups().size());
        assertEquals(MuscleGroup.QUADRICEPS, result.getPrimaryMuscleGroups().get(0));
        assertEquals(ExerciseCategory.BARBELL, result.getCategory());
        assertTrue(result.isCustom());
        assertEquals(10L, result.getCreatedByUserId());
        assertEquals("creator", result.getCreatedByUsername());
    }

    @Test
    void toDTO_HandlesNullExercise() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toSummaryDTO_ShouldMapBasicFields() {
        ExerciseMetadata metadata = new ExerciseMetadata();
        metadata.setName("Deadlift");
        metadata.setImageUrl("dl.jpg");

        Exercise exercise = new Exercise();
        exercise.setId(2L);
        exercise.setMetadata(metadata);
        exercise.setCategory(ExerciseCategory.BARBELL);
        exercise.setPrimaryMuscleGroups(List.of(MuscleGroup.HAMSTRINGS));

        ExerciseSummaryRequest result = mapper.toSummaryDTO(exercise);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Deadlift", result.getName());
        assertEquals("dl.jpg", result.getImageUrl());
        assertEquals(ExerciseCategory.BARBELL, result.getCategory());
    }

    @Test
    void toEntity_ShouldMapDTOToEntity() {
        ExerciseDTO dto = new ExerciseDTO();
        dto.setId(5L);
        dto.setName("Pull Up");
        dto.setDescription("Back exercise");
        dto.setPrimaryMuscleGroups(List.of(MuscleGroup.BACK));
        dto.setCategory(ExerciseCategory.BODYWEIGHT);
        dto.setType(ExerciseType.STRENGTH);
        dto.setCustom(false);

        Exercise result = mapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertNotNull(result.getMetadata());
        assertEquals("Pull Up", result.getMetadata().getName());
        assertEquals("Back exercise", result.getMetadata().getDescription());
        assertEquals(MuscleGroup.BACK, result.getPrimaryMuscleGroups().get(0));
        assertFalse(result.isCustom());
    }
}