package com.danis.ktrack.domain.model.entities;


import com.danis.ktrack.domain.model.valueobject.UserPhysicalProfile;
import com.danis.ktrack.domain.model.valueobject.UserPreferences;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDateTime createdDate;

    @Embedded
    private UserPhysicalProfile physicalProfile;

    @ElementCollection
    @CollectionTable(name = "user_profile_history", joinColumns = @JoinColumn(name = "user_id"))
    @OrderBy("recordedAt DESC")
    private List<UserPhysicalProfile> profileHistory;

    @Embedded
    private UserPreferences preferences;

    @OneToMany(mappedBy = "user")
    private List<Workout> workouts;

    @OneToMany(mappedBy = "createdByUser")
    private List<WorkoutTemplate> templates;

    @OneToMany(mappedBy = "user")
    private List<ExerciseStatistics> statistics;

}
