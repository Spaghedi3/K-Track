package com.danis.backend.restservices;

import com.danis.backend.dto.WorkoutTemplateCreateDTO;
import com.danis.backend.dto.WorkoutTemplateResponseDTO;

import com.danis.backend.dto.WorkoutTemplateUpdateDTO;
import com.danis.backend.service.workflow.WorkoutTemplateWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-templates")
@RequiredArgsConstructor
public class WorkoutTemplateController {

    private final WorkoutTemplateWorkflowService workoutTemplateService;

    @GetMapping
    public List<WorkoutTemplateResponseDTO> getMyTemplates() {
        Long userId = getLoggedInUserId();
        return workoutTemplateService.getAllForUser(userId);
    }

    @PostMapping
    public WorkoutTemplateResponseDTO create(
            @RequestBody WorkoutTemplateCreateDTO dto
    ) {
        Long userId = getLoggedInUserId();
        return workoutTemplateService.create(dto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Long userId = getLoggedInUserId();
        workoutTemplateService.delete(id, userId);
    }
    @PutMapping("/{id}")
    public WorkoutTemplateResponseDTO update(
            @PathVariable Long id,
            @RequestBody WorkoutTemplateUpdateDTO dto
    ) {
        Long userId = getLoggedInUserId();
        return workoutTemplateService.update(id, dto, userId);
    }
    private Long getLoggedInUserId() {
        return Long.parseLong(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }

}
