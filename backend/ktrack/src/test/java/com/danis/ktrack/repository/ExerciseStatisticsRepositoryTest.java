package com.danis.ktrack.repository;

import com.danis.ktrack.domain.model.entities.*;
import com.danis.ktrack.domain.model.valueobject.ExerciseMetadata;
import com.danis.ktrack.domain.repository.ExerciseRepository;
import com.danis.ktrack.domain.repository.ExerciseStatisticsRepository;
import com.danis.ktrack.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ExerciseStatisticsRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ExerciseStatisticsRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void findByUserAndExercise() {
        User user = new User();
        user.setUsername("alex");
        user.setEmail("alex@example.com");
        userRepository.save(user);

        Exercise exercise = new Exercise();
        ExerciseMetadata metadata = new ExerciseMetadata();
        metadata.setName("Bench Press");
        exercise.setMetadata(metadata);
        exerciseRepository.save(exercise);

        ExerciseStatistics stats = new ExerciseStatistics();
        stats.setUser(user);
        stats.setExercise(exercise);
        repository.save(stats);

        Optional<ExerciseStatistics> found = repository.findByUserAndExercise(user, exercise);
        assertThat(found).isPresent();
    }
}
