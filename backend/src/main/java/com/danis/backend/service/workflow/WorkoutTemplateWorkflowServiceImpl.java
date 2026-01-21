package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.*;
import com.danis.backend.domain.repository.ExerciseRepository;
import com.danis.backend.domain.repository.UserRepository;
import com.danis.backend.domain.repository.WorkoutTemplateRepository;
import com.danis.backend.dto.*;
import com.danis.backend.service.validation.WorkoutTemplateValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutTemplateWorkflowServiceImpl
        implements WorkoutTemplateWorkflowService {

    private final WorkoutTemplateRepository templateRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final WorkoutTemplateValidationService validationService;

    @Override
    public WorkoutTemplateResponseDTO create(
            WorkoutTemplateCreateDTO dto,
            Long userId
    ) {
        validationService.validateCreate(dto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutTemplate template = WorkoutTemplate.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .user(user)
                .build();

        template.setExercises(mapExercises(dto, template));
        templateRepository.save(template);

        return toResponse(template);
    }

    @Override
    public List<WorkoutTemplateResponseDTO> getAllForUser(Long userId) {
        return templateRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }
    @Override
    public WorkoutTemplateResponseDTO update(
            Long templateId,
            WorkoutTemplateUpdateDTO dto,
            Long userId
    ) {
        WorkoutTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!template.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // Update basic fields
        if (dto.getName() != null) {
            template.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            template.setDescription(dto.getDescription());
        }

        // Update exercises if provided
        if (dto.getExercises() != null) {
            // Clear existing exercises (orphanRemoval will delete them)
            template.getExercises().clear();

            // Add new exercises
            List<WorkoutTemplateExercise> newExercises = mapExercisesFromUpdate(dto.getExercises(), template);
            template.getExercises().addAll(newExercises);
        }

        // Flush changes to ensure orphans are removed
        templateRepository.saveAndFlush(template);

        return toResponse(template);
    }

    @Override
    public void delete(Long templateId, Long userId) {
        WorkoutTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!template.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        templateRepository.delete(template);
    }

    /* ----------------- Mapping ----------------- */

    private List<WorkoutTemplateExercise> mapExercises(
            WorkoutTemplateCreateDTO dto,
            WorkoutTemplate template
    ) {
        return dto.getExercises().stream().map(exDto -> {

            Exercise exercise = exerciseRepository.findById(exDto.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found"));

            WorkoutTemplateExercise templateExercise =
                    WorkoutTemplateExercise.builder()
                            .exercise(exercise)
                            .orderIndex(exDto.getOrderIndex())
                            .workoutTemplate(template)
                            .build();

            templateExercise.setSets(
                    exDto.getSets().stream().map(setDto ->
                            WorkoutTemplateSet.builder()
                                    .reps(setDto.getReps())
                                    .weight(setDto.getWeight())
                                    .durationSeconds(setDto.getDurationSeconds())
                                    .distance(setDto.getDistance())
                                    .templateExercise(templateExercise)
                                    .build()
                    ).toList()
            );

            return templateExercise;
        }).toList();
    }
    private List<WorkoutTemplateExercise> mapExercisesFromUpdate(
            List<WorkoutTemplateExerciseUpdateDTO> exerciseDTOs,
            WorkoutTemplate template
    ) {
        return exerciseDTOs.stream().map(exDto -> {
            Exercise exercise = exerciseRepository.findById(exDto.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found"));

            WorkoutTemplateExercise templateExercise =
                    WorkoutTemplateExercise.builder()
                            .exercise(exercise)
                            .orderIndex(exDto.getOrderIndex())
                            .workoutTemplate(template)
                            .build();

            templateExercise.setSets(
                    exDto.getSets().stream().map(setDto ->
                            WorkoutTemplateSet.builder()
                                    .reps(setDto.getReps())
                                    .weight(setDto.getWeight())
                                    .durationSeconds(setDto.getDurationSeconds())
                                    .distance(setDto.getDistance())
                                    .templateExercise(templateExercise)
                                    .build()
                    ).toList()
            );

            return templateExercise;
        }).toList();
    }

    private WorkoutTemplateResponseDTO toResponse(WorkoutTemplate template) {
        return WorkoutTemplateResponseDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .exercises(
                        template.getExercises().stream().map(ex ->
                                WorkoutTemplateExerciseResponseDTO.from(ex)
                        ).toList()
                )
                .build();
    }
}
