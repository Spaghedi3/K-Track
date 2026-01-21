package com.danis.backend.service.validation;

import com.danis.backend.dto.WorkoutTemplateCreateDTO;
import org.springframework.stereotype.Service;


public  interface WorkoutTemplateValidationService {
    void validateCreate(WorkoutTemplateCreateDTO dto);
}
