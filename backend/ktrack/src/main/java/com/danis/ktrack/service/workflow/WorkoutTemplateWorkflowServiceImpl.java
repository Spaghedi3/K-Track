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
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorUserId));

        newTemplate.setCreatedByUser(creator);


        validationService.validate(newTemplate);

        return templateRepository.save(newTemplate);
    }

    @Override
    @Transactional
    public WorkoutTemplate updateTemplate(Long templateId, WorkoutTemplate templateDetails) throws ValidationException {

        validationService.validate(templateDetails);

        WorkoutTemplate existingTemplate = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("WorkoutTemplate not found with id: " + templateId));


        existingTemplate.setName(templateDetails.getName());
        existingTemplate.setDescription(templateDetails.getDescription());
        existingTemplate.setTags(templateDetails.getTags());

        return templateRepository.save(existingTemplate);
    }
}