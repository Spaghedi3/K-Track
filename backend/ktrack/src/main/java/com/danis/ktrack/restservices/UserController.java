package com.danis.ktrack.restservices;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.dto.CreateUserRequest;
import com.danis.ktrack.dto.UpdateUserRequest;
import com.danis.ktrack.dto.UserDTO;
import com.danis.ktrack.dto.UserMapper;
import com.danis.ktrack.service.workflow.UserWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for User management
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserWorkflowService userWorkflowService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserWorkflowService userWorkflowService,
                          UserRepository userRepository,
                          UserMapper userMapper) {
        this.userWorkflowService = userWorkflowService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Create a new user (register)
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        User newUser = userMapper.toEntity(request);
        User savedUser = userWorkflowService.registerUser(newUser);
        UserDTO dto = userMapper.toDTO(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        UserDTO dto = userMapper.toDTO(user);
        return ResponseEntity.ok(dto);
    }

    /**
     * Get user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        UserDTO dto = userMapper.toDTO(user);
        return ResponseEntity.ok(dto);
    }

    /**
     * Get user by email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        UserDTO dto = userMapper.toDTO(user);
        return ResponseEntity.ok(dto);
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> dtos = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Update user profile
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateEntityFromRequest(existingUser, request);
        User updatedUser = userWorkflowService.updateUserProfile(id, existingUser);
        UserDTO dto = userMapper.toDTO(updatedUser);
        return ResponseEntity.ok(dto);
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}