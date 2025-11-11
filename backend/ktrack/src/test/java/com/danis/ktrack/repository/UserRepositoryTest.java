package com.danis.ktrack.repository;


import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by username")
    void findByUsername() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("john");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail() {
        User user = new User();
        user.setUsername("jane");
        user.setEmail("jane@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("jane@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("jane");
    }
}
