package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.WorkoutTemplate;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutTemplateRepository;
import com.danis.ktrack.service.validation.ValidationException;
import com.danis.ktrack.service.validation.WorkoutTemplateValidationService;

public interface WorkoutTemplateWorkflowService {

    /**
     * Use Case: Create Workout Template
     * Validates and creates a new workout template for a specific user.
     *
     * @param newTemplate     The WorkoutTemplate object with details.
     * @param creatorUserId The ID of the user creating this template.
     * @return The persisted WorkoutTemplate entity.
     * @throws ValidationException if the template data is invalid.
     * @throws RuntimeException    if the creator user is not found.
     */
    WorkoutTemplate createTemplate(WorkoutTemplate newTemplate, Long creatorUserId) throws ValidationException;

    /**
     * Use Case: Update Workout Template
     * Validates and updates an existing workout template's details.
     *
     * @param templateId      The ID of the template to update.
     * @param templateDetails WorkoutTemplate object with the new details.
     * @return The updated WorkoutTemplate entity.
     * @throws ValidationException if the new template data is invalid.
     * @throws RuntimeException    if the template is not found.
     */
    WorkoutTemplate updateTemplate(Long templateId, WorkoutTemplate templateDetails) throws ValidationException;
}