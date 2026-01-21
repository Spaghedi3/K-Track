package com.danis.backend.service.workflow;

import com.danis.backend.dto.WorkoutTemplateCreateDTO;
import com.danis.backend.dto.WorkoutTemplateResponseDTO;
import com.danis.backend.dto.WorkoutTemplateUpdateDTO;


import java.util.List;

public interface WorkoutTemplateWorkflowService {

    WorkoutTemplateResponseDTO create(WorkoutTemplateCreateDTO dto, Long userId);

    List<WorkoutTemplateResponseDTO> getAllForUser(Long userId);

    WorkoutTemplateResponseDTO update(Long templateId, WorkoutTemplateUpdateDTO dto, Long userId);

    void delete(Long templateId, Long userId);
}
