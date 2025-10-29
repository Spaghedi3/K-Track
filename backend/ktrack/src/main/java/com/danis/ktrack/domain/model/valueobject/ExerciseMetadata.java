package com.danis.ktrack.domain.model.valueobject;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseMetadata {
private String name;
private String description;
private String instructions;
private String videoUrl;
private String imageUrl;
}
