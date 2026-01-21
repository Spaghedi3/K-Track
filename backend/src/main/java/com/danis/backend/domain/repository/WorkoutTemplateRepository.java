package com.danis.backend.domain.repository;


import com.danis.backend.domain.model.entities.WorkoutTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {

    List<WorkoutTemplate> findAllByUserId(Long userId);
}
