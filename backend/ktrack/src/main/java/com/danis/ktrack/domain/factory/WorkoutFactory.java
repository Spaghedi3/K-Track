package com.danis.ktrack.domain.factory;


import com.danis.ktrack.domain.model.entities.*;
import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import com.danis.ktrack.domain.model.valueobject.WorkoutPeriod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkoutFactory {

public Workout createWorkoutFromTemplate(User user, WorkoutTemplate workoutTemplate)
{
    Workout workout = new Workout();
    workout.setUser(user);
    workout.setTemplate(workoutTemplate);
    workout.setName(workoutTemplate.getName());
    workout.setStatus(WorkoutStatus.IN_PROGRESS);
    workout.setDate(LocalDate.now());
    workout.setCreatedAt(LocalDateTime.now());

    WorkoutPeriod period = new WorkoutPeriod();
    period.setStartTime(LocalDateTime.now());
    workout.setPeriod(period);

    if(workoutTemplate.getTemplateExercises()!=null)
    {
        List<WorkoutExercise> workoutExercises = workoutTemplate.getTemplateExercises().stream()
                .map(templateExercise -> createWorkoutExerciseFromTemplate(workout,templateExercise))
                .collect(Collectors.toList());

        workout.setWorkoutExercises(workoutExercises);
        }
    else {
        workout.setWorkoutExercises(new ArrayList<>());
    }
    return workout;

}

private WorkoutExercise createWorkoutExerciseFromTemplate(Workout parentWorkout, TemplateExercise templateExercise)
{
    WorkoutExercise workoutExercise = new WorkoutExercise();

    workoutExercise.setExercise(templateExercise.getExercise());
    workoutExercise.setOrderIndex(templateExercise.getOrderIndex());
    workoutExercise.setNotes(templateExercise.getNotes());

    workoutExercise.setSets(new ArrayList<>());

    return  workoutExercise;
}

    public Workout createBlankWorkout(User user, String name) {
        Workout workout = new Workout();
        workout.setUser(user);
        workout.setName(name);
        workout.setStatus(WorkoutStatus.IN_PROGRESS);
        workout.setDate(LocalDate.now());
        workout.setCreatedAt(LocalDateTime.now());
        workout.setWorkoutExercises(new ArrayList<>());

        WorkoutPeriod period = new WorkoutPeriod();
        period.setStartTime(LocalDateTime.now());
        workout.setPeriod(period);

        return workout;
    }
}
