package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.WorkoutTemplate;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutTemplateRepository;
import com.danis.ktrack.service.validation.ValidationException;
import com.danis.ktrack.service.validation.WorkoutTemplateValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutTemplateWorkflowServiceImplTest {

    @Mock
    private WorkoutTemplateRepository templateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutTemplateValidationService validationService;

    @InjectMocks
    private WorkoutTemplateWorkflowServiceImpl service;

    private User testUser;
    private WorkoutTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTemplate = new WorkoutTemplate();
        testTemplate.setName("Test Template");
        testTemplate.setCreatedByUser(testUser); // Set by service, but good for details object
        testTemplate.setTemplateExercises(List.of()); // Mock non-empty for validation
        testTemplate.setTags(List.of());
        testTemplate.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createTemplate_Success() throws ValidationException {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(validationService).validate(any(WorkoutTemplate.class));
        when(templateRepository.save(any(WorkoutTemplate.class))).thenReturn(testTemplate);

        WorkoutTemplate newTemplate = new WorkoutTemplate();
        newTemplate.setName("Test Template");
        newTemplate.setTemplateExercises(List.of()); // Set fields required by validation
        newTemplate.setTags(List.of());
        newTemplate.setCreatedAt(LocalDateTime.now());


        // Act
        WorkoutTemplate savedTemplate = service.createTemplate(newTemplate, 1L);

        // Assert
        assertNotNull(savedTemplate);
        assertEquals("Test Template", savedTemplate.getName());
        assertEquals(1L, savedTemplate.getCreatedByUser().getId());

        verify(userRepository, times(1)).findById(1L);
        verify(validationService, times(1)).validate(newTemplate);
        verify(templateRepository, times(1)).save(newTemplate);
    }

    @Test
    void createTemplate_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.createTemplate(testTemplate, 1L);
        });

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(validationService, never()).validate(any());
        verify(templateRepository, never()).save(any());
    }

    @Test
    void createTemplate_InvalidTemplate_ShouldThrowValidationException() throws ValidationException {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doThrow(new ValidationException(List.of("Name is empty")))
                .when(validationService).validate(any(WorkoutTemplate.class));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            service.createTemplate(testTemplate, 1L);
        });

        assertTrue(exception.getErrors().contains("Name is empty"));
        verify(templateRepository, never()).save(any());
    }

    @Test
    void updateTemplate_Success() throws ValidationException {
        // Arrange
        WorkoutTemplate existingTemplate = new WorkoutTemplate();
        existingTemplate.setId(1L);
        existingTemplate.setName("Old Name");

        WorkoutTemplate details = new WorkoutTemplate();
        details.setName("New Name");
        details.setDescription("New Desc");
        details.setTags(List.of("new"));
        // Set fields required for validation
        details.setCreatedByUser(testUser);
        details.setTemplateExercises(List.of());
        details.setCreatedAt(LocalDateTime.now());


        doNothing().when(validationService).validate(details);
        when(templateRepository.findById(1L)).thenReturn(Optional.of(existingTemplate));
        when(templateRepository.save(existingTemplate)).thenReturn(existingTemplate);

        // Act
        WorkoutTemplate updatedTemplate = service.updateTemplate(1L, details);

        // Assert
        assertNotNull(updatedTemplate);
        assertEquals("New Name", updatedTemplate.getName());
        assertEquals("New Desc", updatedTemplate.getDescription());
        assertEquals(List.of("new"), updatedTemplate.getTags());

        verify(validationService, times(1)).validate(details);
        verify(templateRepository, times(1)).findById(1L);
        verify(templateRepository, times(1)).save(existingTemplate);
    }

    @Test
    void updateTemplate_ValidationFails_ShouldThrowException() throws ValidationException {
        // Arrange
        doThrow(new ValidationException(List.of("Name is empty")))
                .when(validationService).validate(testTemplate);

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            service.updateTemplate(1L, testTemplate);
        });

        verify(templateRepository, never()).findById(any());
        verify(templateRepository, never()).save(any());
    }

    @Test
    void updateTemplate_NotFound_ShouldThrowException() throws ValidationException {
        // Arrange
        doNothing().when(validationService).validate(testTemplate);
        when(templateRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateTemplate(1L, testTemplate);
        });

        assertEquals("WorkoutTemplate not found with id: 1", exception.getMessage());
        verify(templateRepository, never()).save(any());
    }
}