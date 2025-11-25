package com.danis.ktrack.dto.exercise;

import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.entities.ExerciseStatistics;
import com.danis.ktrack.domain.model.entities.VolumeDataPoint;
import com.danis.ktrack.domain.model.valueobject.ExerciseMetadata;
import com.danis.ktrack.domain.model.valueobject.PersonalRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ExerciseMapper {

    // ... toDTO and toSummaryDTO are fine, keeping them brief ...
    public ExerciseDTO toDTO(Exercise exercise) {
        if (exercise == null) return null;
        ExerciseDTO dto = new ExerciseDTO();
        dto.setId(exercise.getId());
        if (exercise.getMetadata() != null) {
            dto.setName(exercise.getMetadata().getName());
            dto.setDescription(exercise.getMetadata().getDescription());
            dto.setInstructions(exercise.getMetadata().getInstructions());
            dto.setVideoUrl(exercise.getMetadata().getVideoUrl());
            dto.setImageUrl(exercise.getMetadata().getImageUrl());
        }
        dto.setPrimaryMuscleGroups(exercise.getPrimaryMuscleGroups());
        dto.setSecondaryMuscleGroups(exercise.getSecondaryMuscleGroups());
        dto.setCategory(exercise.getCategory());
        dto.setType(exercise.getType());
        dto.setCustom(exercise.isCustom());

        if (exercise.getCreatedByUser() != null) {
            dto.setCreatedByUserId(exercise.getCreatedByUser().getId());
            dto.setCreatedByUsername(exercise.getCreatedByUser().getUsername());
        }

        // Auditable fields
        // Note: Make sure ExerciseDTO has these fields, otherwise remove these lines
        dto.setCreatedAt(exercise.getCreatedAt());
        // dto.setLastModifiedAt(exercise.getLastModifiedAt());
        return dto;
    }

    public ExerciseSummaryRequest toSummaryDTO(Exercise exercise) {
        if (exercise == null) return null;
        ExerciseSummaryRequest dto = new ExerciseSummaryRequest();
        dto.setId(exercise.getId());
        if (exercise.getMetadata() != null) {
            dto.setName(exercise.getMetadata().getName());
            dto.setImageUrl(exercise.getMetadata().getImageUrl());
        }
        dto.setPrimaryMuscleGroups(exercise.getPrimaryMuscleGroups());
        dto.setCategory(exercise.getCategory());
        dto.setType(exercise.getType());
        dto.setCustom(exercise.isCustom());
        return dto;
    }

    /**
     * FIXED: Changed return type from ExerciseStatistics to ExerciseStatisticsRequest
     */
    public ExerciseStatisticsRequest toStatisticsDTO(ExerciseStatistics statistics) {
        if (statistics == null) {
            return null;
        }

        ExerciseStatisticsRequest dto = new ExerciseStatisticsRequest();
        dto.setId(statistics.getId());

        if (statistics.getExercise() != null) {
            dto.setExerciseId(statistics.getExercise().getId());
            if (statistics.getExercise().getMetadata() != null) {
                dto.setExerciseName(statistics.getExercise().getMetadata().getName());
            }
        }

        if (statistics.getUser() != null) {
            dto.setUserId(statistics.getUser().getId());
            dto.setUsername(statistics.getUser().getUsername());
        }

        dto.setTotalWorkouts(statistics.getTotalWorkouts());
        dto.setTotalSets(statistics.getTotalSets());
        dto.setTotalReps(statistics.getTotalReps());
        dto.setTotalVolume(statistics.getTotalVolume());
        dto.setLastPerformed(statistics.getLastPerformed());

        if (statistics.getPersonalRecords() != null) {
            dto.setPersonalRecords(
                    statistics.getPersonalRecords().stream()
                            .map(this::toPersonalRecordDTO)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setPersonalRecords(new ArrayList<>());
        }

        if (statistics.getVolumeHistory() != null) {
            dto.setVolumeHistory(
                    statistics.getVolumeHistory().stream()
                            .map(this::toVolumeDataPointDTO)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setVolumeHistory(new ArrayList<>());
        }

        return dto;
    }

    public PersonalRecordRequest toPersonalRecordDTO(PersonalRecord record) {
        if (record == null) {
            return null;
        }

        PersonalRecordRequest dto = new PersonalRecordRequest();

        // FIXED: Convert String ID from VO to Long ID for DTO
        if (record.getExerciseId() != null) {
            dto.setExerciseId(Long.valueOf(record.getExerciseId()));
        }

        // FIXED: Convert String ID from VO to Long ID for DTO
        if (record.getWorkoutId() != null) {
            dto.setWorkoutId(Long.valueOf(record.getWorkoutId()));
        }

        dto.setType(record.getType());
        dto.setValue(record.getValue());
        dto.setAchievedDate(record.getAchievedDate());

        return dto;
    }

    public VolumeDataPointRequest toVolumeDataPointDTO(VolumeDataPoint dataPoint) {
        if (dataPoint == null) {
            return null;
        }

        VolumeDataPointRequest dto = new VolumeDataPointRequest();
        dto.setId(dataPoint.getId());
        dto.setDate(dataPoint.getDate());

        if (dataPoint.getMetrics() != null) {
            dto.setTotalVolume(dataPoint.getMetrics().getTotalVolume());
            dto.setTotalSets(dataPoint.getMetrics().getTotalSets());
            dto.setTotalReps(dataPoint.getMetrics().getTotalReps());
            dto.setWeight(dataPoint.getMetrics().getWeight());
        }

        if (dataPoint.getWorkout() != null) {
            dto.setWorkoutId(dataPoint.getWorkout().getId());
        }

        return dto;
    }

    public PersonalRecord toPersonalRecordEntity(PersonalRecordRequest dto) {
        if (dto == null) {
            return null;
        }

        PersonalRecord record = new PersonalRecord();

        // FIXED: Convert Long ID from DTO to String ID for VO
        if (dto.getExerciseId() != null) {
            record.setExerciseId(String.valueOf(dto.getExerciseId()));
        }

        if (dto.getWorkoutId() != null) {
            record.setWorkoutId(String.valueOf(dto.getWorkoutId()));
        }

        record.setType(dto.getType());
        record.setValue(dto.getValue());
        record.setAchievedDate(dto.getAchievedDate());

        return record;
    }
    public Exercise toEntity(ExerciseDTO dto) {
        if (dto == null) {
            return null;
        }

        Exercise exercise = new Exercise();
        exercise.setId(dto.getId());

        // Create Metadata object
        ExerciseMetadata metadata = new ExerciseMetadata();
        metadata.setName(dto.getName());
        metadata.setDescription(dto.getDescription());
        metadata.setInstructions(dto.getInstructions());
        metadata.setVideoUrl(dto.getVideoUrl());
        metadata.setImageUrl(dto.getImageUrl());
        exercise.setMetadata(metadata);

        exercise.setPrimaryMuscleGroups(dto.getPrimaryMuscleGroups());
        exercise.setSecondaryMuscleGroups(dto.getSecondaryMuscleGroups());
        exercise.setCategory(dto.getCategory());
        exercise.setType(dto.getType());
        exercise.setCustom(dto.isCustom());

        return exercise;
    }
}