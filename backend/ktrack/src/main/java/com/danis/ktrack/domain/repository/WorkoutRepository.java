package com.danis.ktrack.domain.repository;


import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long>
{
    List<Workout> findByUser(User user);

    List<Workout> findByUserOrderByDateDesc(User user);
}
