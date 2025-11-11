package com.danis.ktrack.repository;


import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.domain.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkoutRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdWithExercisesAndSets() {
        User user = new User();
        user.setUsername("alex");
        user.setEmail("alex@example.com");
        userRepository.save(user);

        Workout workout = new Workout();
        workout.setName("Leg Day");
        workout.setUser(user); // 🔥 AICI e cheia
        workout = workoutRepository.save(workout);


        Optional<Workout> found = workoutRepository.findByIdWithExercises(workout.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Leg Day");
        assertThat(found.get().getUser().getUsername()).isEqualTo("alex");
    }
}
