package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import com.danis.ktrack.domain.model.valueobject.WorkoutPeriod;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutRepository;
import com.danis.ktrack.service.validation.ValidationException;
import com.danis.ktrack.service.validation.WorkoutValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutWorkflowServiceImplTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutValidationService validationService;

    @InjectMocks
    private WorkoutWorkflowServiceImpl workoutWorkflowService;

    private User mockUser;
    private Workout mockNewWorkout;
    private Workout mockExistingWorkout;
    private Workout mockUpdateDetails;
    private final Long userId = 1L;
    private final Long workoutId = 10L;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);

        mockNewWorkout = mock(Workout.class);

        mockExistingWorkout = mock(Workout.class);

        mockUpdateDetails = mock(Workout.class);

    }

    @Test
    void startWorkout_success() throws ValidationException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(workoutRepository.save(mockNewWorkout)).thenReturn(mockNewWorkout);

        Workout result = workoutWorkflowService.startWorkout(mockNewWorkout, userId);

        assertNotNull(result);
        assertEquals(mockNewWorkout, result);

        verify(userRepository).findById(userId);
        verify(mockNewWorkout).setUser(mockUser);
        verify(validationService).validate(mockNewWorkout);
        verify(workoutRepository).save(mockNewWorkout);
    }

    @Test
    void startWorkout_userNotFound_throwsRuntimeException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            workoutWorkflowService.startWorkout(mockNewWorkout, userId);
        });

        assertTrue(exception.getMessage().contains("User not found"));

        verify(userRepository).findById(userId);
        verifyNoInteractions(validationService);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void startWorkout_validationFails_throwsValidationException() throws ValidationException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doThrow(new ValidationException(List.of("Invalid workout data"))).when(validationService).validate(mockNewWorkout);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutWorkflowService.startWorkout(mockNewWorkout, userId);
        });

        assertTrue(exception.getMessage().contains("Invalid workout data"));

        verify(userRepository).findById(userId);
        verify(mockNewWorkout).setUser(mockUser);
        verify(validationService).validate(mockNewWorkout);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void updateWorkout_success() throws ValidationException {
        WorkoutPeriod mockWorkoutPeriod = mock(WorkoutPeriod.class);
        WorkoutStatus mockWorkoutStatus = mock(WorkoutStatus.class);

        when(mockUpdateDetails.getName()).thenReturn("Updated Workout Name");
        when(mockUpdateDetails.getDate()).thenReturn(LocalDate.now().plusDays(1));
        when(mockUpdateDetails.getStatus()).thenReturn(mockWorkoutStatus);
        when(mockUpdateDetails.getNotes()).thenReturn("Some new notes");
        when(mockUpdateDetails.getPeriod()).thenReturn(mockWorkoutPeriod);


        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(mockExistingWorkout));
        when(workoutRepository.save(mockExistingWorkout)).thenReturn(mockExistingWorkout);

        Workout result = workoutWorkflowService.updateWorkout(workoutId, mockUpdateDetails);

        assertNotNull(result);
        assertEquals(mockExistingWorkout, result);

        verify(validationService).validate(mockUpdateDetails);
        verify(workoutRepository).findById(workoutId);

        verify(mockExistingWorkout).setName(mockUpdateDetails.getName());
        verify(mockExistingWorkout).setDate(mockUpdateDetails.getDate());
        verify(mockExistingWorkout).setStatus(mockUpdateDetails.getStatus());
        verify(mockExistingWorkout).setNotes(mockUpdateDetails.getNotes());
        verify(mockExistingWorkout).setPeriod(mockUpdateDetails.getPeriod());

        verify(workoutRepository).save(mockExistingWorkout);
    }

    @Test
    void updateWorkout_workoutNotFound_throwsRuntimeException() throws ValidationException {
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            workoutWorkflowService.updateWorkout(workoutId, mockUpdateDetails);
        });

        assertTrue(exception.getMessage().contains("Workout not found"));

        verify(validationService).validate(mockUpdateDetails);
        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository, never()).save(any());

        verifyNoMoreInteractions(mockExistingWorkout);
    }

    @Test
    void updateWorkout_validationFails_throwsValidationException() throws ValidationException {
        doThrow(new ValidationException(List.of("Invalid update data"))).when(validationService).validate(mockUpdateDetails);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutWorkflowService.updateWorkout(workoutId, mockUpdateDetails);
        });

        assertTrue(exception.getMessage().contains("Invalid update data"));

        verify(validationService).validate(mockUpdateDetails);
        verify(workoutRepository, never()).findById(any());
        verify(workoutRepository, never()).save(any());
    }
}