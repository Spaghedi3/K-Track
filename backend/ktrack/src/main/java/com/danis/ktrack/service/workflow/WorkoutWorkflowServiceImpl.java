package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutRepository;
import com.danis.ktrack.service.validation.ValidationException;
import com.danis.ktrack.service.validation.WorkoutValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutWorkflowServiceImpl implements WorkoutWorkflowService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final WorkoutValidationService validationService;

    public WorkoutWorkflowServiceImpl(WorkoutRepository workoutRepository,
                                      UserRepository userRepository,
                                      WorkoutValidationService validationService) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public Workout startWorkout(Workout newWorkout, Long userId) throws ValidationException {
        // 1. FIND DEPENDENCIES
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2. SET UP ENTITY
        newWorkout.setUser(user);
        // The validation service checks if the user is null

        // 3. VALIDATION
        validationService.validate(newWorkout);

        // 4. PERSISTENCE
        return workoutRepository.save(newWorkout);
    }

    @Override
    @Transactional
    public Workout updateWorkout(Long workoutId, Workout workoutDetails) throws ValidationException {
        // 1. VALIDATION
        // Validate the new details provided.
        validationService.validate(workoutDetails);

        // 2. FIND ENTITY
        Workout existingWorkout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        // 3. UPDATE & PERSIST
        // Update the fields on the managed entity.
        existingWorkout.setName(workoutDetails.getName());
        existingWorkout.setDate(workoutDetails.getDate());
        existingWorkout.setStatus(workoutDetails.getStatus());
        existingWorkout.setNotes(workoutDetails.getNotes());
        existingWorkout.setPeriod(workoutDetails.getPeriod());

        return workoutRepository.save(existingWorkout);
    }
}
