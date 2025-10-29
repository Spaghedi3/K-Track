package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.WorkoutTemplate;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutTemplateRepository;
import com.danis.ktrack.service.validation.ValidationException;
import com.danis.ktrack.service.validation.WorkoutTemplateValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutTemplateWorkflowServiceImpl implements WorkoutTemplateWorkflowService {

    private final WorkoutTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final WorkoutTemplateValidationService validationService;

    public WorkoutTemplateWorkflowServiceImpl(WorkoutTemplateRepository templateRepository,
                                              UserRepository userRepository,
                                              WorkoutTemplateValidationService validationService) {
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public WorkoutTemplate createTemplate(WorkoutTemplate newTemplate, Long creatorUserId) throws ValidationException {
        // 1. FIND DEPENDENCIES
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorUserId));

        // 2. SET UP ENTITY
        newTemplate.setCreatedByUser(creator);
        // The validation service checks if the creator is null
        // so we must set it *before* validating.

        // 3. VALIDATION
        validationService.validate(newTemplate);

        // 4. PERSISTENCE
        return templateRepository.save(newTemplate);
    }

    @Override
    @Transactional
    public WorkoutTemplate updateTemplate(Long templateId, WorkoutTemplate templateDetails) throws ValidationException {
        // 1. VALIDATION
        // Validate the new details *before* querying the database.
        // This avoids validating a creator, as the existing template already has one.
        // Note: You may need to adjust validation logic if it incorrectly flags
        // a missing 'createdByUser' field on the 'templateDetails' DTO.
        validationService.validate(templateDetails);

        // 2. FIND ENTITY
        WorkoutTemplate existingTemplate = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("WorkoutTemplate not found with id: " + templateId));

        // 3. UPDATE & PERSIST
        // Update fields on the existing, managed entity.
        existingTemplate.setName(templateDetails.getName());
        existingTemplate.setDescription(templateDetails.getDescription());
        existingTemplate.setTags(templateDetails.getTags());
        // Add any other fields that are safe to update.

        return templateRepository.save(existingTemplate);
    }
}