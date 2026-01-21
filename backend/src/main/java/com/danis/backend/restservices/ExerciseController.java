package com.danis.backend.restservices;

import com.danis.backend.domain.model.entities.Exercise;
import com.danis.backend.dto.ExerciseCreateDTO;
import com.danis.backend.dto.ExerciseResponseDTO;
import com.danis.backend.service.workflow.ExerciseWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseWorkflowService exerciseService;

    @GetMapping("/custom")
    public List<ExerciseResponseDTO> getCustomExercises() {
        Long userId = getLoggedInUserId();

        return exerciseService.getCustomExercises(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/preloaded")
    public List<ExerciseResponseDTO> getPreloadedExercises() {
        return exerciseService.getPreloadedExercises().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/custom")
    public ExerciseResponseDTO createCustom(@RequestBody ExerciseCreateDTO dto) {
        Long userId = getLoggedInUserId();
        Exercise saved = exerciseService.createCustomExercise(dto, userId);
        return toDTO(saved);
    }

    private Long getLoggedInUserId() {
        return Long.parseLong(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());
    }

    private ExerciseResponseDTO toDTO(Exercise e) {
        return ExerciseResponseDTO.builder()
                .id(e.getId())
                .exerciseId(e.getExerciseId())
                .name(e.getName())
                .imageUrl(e.getImageUrl())
                .videoUrl(e.getVideoUrl())
                .overview(e.getOverview())
                .equipments(e.getEquipments())
                .bodyParts(e.getBodyParts())
                .targetMuscles(e.getTargetMuscles())
                .secondaryMuscles(e.getSecondaryMuscles())
                .keywords(e.getKeywords())
                .instructions(e.getInstructions())
                .exerciseTips(e.getExerciseTips())
                .variations(e.getVariations())
                .relatedExerciseIds(e.getRelatedExerciseIds())
                .type(e.getType().name())
                .build();
    }
    @DeleteMapping("/custom/{id}")
    public void deleteCustomExercise(@PathVariable Long id) {
        Long userId = getLoggedInUserId();
        exerciseService.deleteCustomExercise(id, userId);
    }

}
