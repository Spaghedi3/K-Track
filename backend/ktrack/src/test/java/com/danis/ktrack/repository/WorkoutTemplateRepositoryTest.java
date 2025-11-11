package com.danis.ktrack.repository;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.WorkoutTemplate;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkoutTemplateRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WorkoutTemplateRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByCreatedByUser() {
        User user = new User();
        user.setUsername("coach");
        user.setEmail("coach@example.com");
        userRepository.save(user);

        WorkoutTemplate template = new WorkoutTemplate();
        template.setCreatedByUser(user);
        repository.save(template);

        List<WorkoutTemplate> found = repository.findBycreatedByUser(user);
        assertThat(found).hasSize(1);
    }
}
